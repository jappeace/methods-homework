extern crate rand;
use skater::{Skater, Point, Preference};


// learn strategy:
// choose from onlinelearn, egreedy, greedy (without explore)
pub type selectActionFunction = fn(&learnFunction, &mut Skater, Vec<Point>) -> usize;
pub type learnFunction = fn(&mut Skater, usize, f64);

pub fn mixed(learnF:&learnFunction, skater:&mut Skater, skaterPositions:Vec<Point>) -> usize{
    let rewards:Vec<f64> = skater.actionOpinions.clone().into_iter().map(
        |opinion:Preference| opinion.lastReward
    ).collect();
    let totalReward = rewards.iter().fold(0.0,|cur,prev| cur+prev);
    let probablities = rewards.into_iter().map(|x| x/totalReward);
    let mut chosenIndex:usize = 0;
    { // here I gave up on recursion, and found folding to complicated, so I went back
        // to my imperative roots.
        let choice = rand::random::<f64>();
        let mut probstack = 0.0;
        for probability in probablities{
            probstack += probability;
            chosenIndex += 1;
            if choice < probstack { // and I wanted to use a library for this.
                break;
            }
        }
        chosenIndex -= 1;
    }
    learn(learnF, skater, skaterPositions, chosenIndex);
    return chosenIndex;
}
fn learn(learnF:&learnFunction, skater:&mut Skater, skaterPositions:Vec<Point>, choice:usize){
    let reward = skater.determineReward(skaterPositions, choice);
    learnF(skater, choice, reward);
    skater.actionOpinions[choice].uses += 1;
}

use rand::Rng;
pub fn egreedy(learnF:&learnFunction, skater:&mut Skater, skaterPositions:Vec<Point>) -> usize{
    let episolon = 0.1;
    let greed = rand::random::<f64>();
    let choice:usize = if greed <= episolon{
        // explore
        rand::thread_rng().gen_range(0, skater.availableActions.len())
    }else{
        let start:(usize,f64) = (0, -skater.rewards.unreasonablyHigh);
        skater.actionOpinions.iter().enumerate().fold(start, |prev,(index,preference)| {
            if prev.1 < preference.lastReward {
                return (index,preference.lastReward);
            }
            return prev;
        }).0
    };
    learn(learnF, skater, skaterPositions, choice);
    return choice;
}

pub fn greedy(learnF:&learnFunction, skater:&mut Skater, skaterPositions:Vec<Point>) -> usize{
    let start:(usize,f64) = (0, -skater.rewards.unreasonablyHigh);
    let choice:usize = skater.actionOpinions.iter().enumerate().fold(start, |prev,(index,preference)| {
        if prev.1 < preference.lastReward {
            return (index,preference.lastReward);
        }
        return prev;
    }).0;
    learn(learnF, skater, skaterPositions, choice);
    return choice;
}

// learn slower and slower
pub fn regresiveLearning(skater:&mut Skater, action:usize, newReward:f64){
    // oldvalue + learnrate (reward - oldvalue)
    let oldReward = skater.actionOpinions[action].lastReward;
    // where learnrate = (1/n)
    let learnrate= 1.0 / skater.actionOpinions[action].uses as f64; 
    skater.actionOpinions[action].lastReward = oldReward + learnrate * (newReward - oldReward);
}

static learnRate:f64 = 0.2;
pub fn constantLearning(skater:&mut Skater, action:usize, newReward:f64) {
    // oldvalue + learnrate (reward - oldvalue)
    let oldReward = skater.actionOpinions[action].lastReward;
    // where learnrate = (1/n)
    skater.actionOpinions[action].lastReward = oldReward + learnRate * (newReward - oldReward);
}
