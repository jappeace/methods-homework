// This programs simulates a primitive reinforcement learning scanerio
// Copyright (C) 2016 Jappie Klooster

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.If not, see <http://www.gnu.org/licenses/>.

// shut up rust
#![allow(dead_code)]
#![allow(non_upper_case_globals)]
#![allow(non_snake_case)]

extern crate rand;
extern crate gnuplot;

// Configuration
static simulationCount:i64= 1000;
static skaterCount:i64 = 31;
static directionChoices:&'static [f64;6] = &[0.0,60.0,120.0,180.0,240.0,300.0];
static speed:f64 = 0.5;
static collisionRadius:f64 = 1.0;
static SPACE:Space = Space {
    width: 10,
    height: 10
};
static Rewards:Reward = Reward{
    collision:-1.0,
    avoided:20.0,
    unreasonablyHigh:100.0
};
// learn strategy:
// choose from onlinelearn, egreedy, greedy (without explore)
static strategy:selectActionFunction = greedy;
static learnStrat:learnFunction = constantLearning;

type selectActionFunction = fn(&mut Skater, Vec<Point>) -> usize;
type learnFunction = fn(&mut Skater, usize, f64);

// Structures
struct Reward{
    collision:f64,
    avoided:f64,
    unreasonablyHigh:f64
}
struct Space{
    width:i64,
    height:i64
}
#[derive(Clone)]
#[derive(Debug)]
struct Point {
    x: f64,
    y: f64,
}
#[derive(Clone)]
#[derive(Debug)]
struct Preference{
    uses:i64,
    lastReward:f64
}
#[derive(Clone)]
struct Skater{
    actionOpinions:Vec<Preference>, // same size as the action set
    position:Point,
}

impl Skater{

    fn new() -> Skater{
        let defaultPrefs = directionChoices.into_iter().map( |&_|
            Preference{
                uses:1,
                lastReward:Rewards.unreasonablyHigh
            }
        ).collect();
        return Skater{
            actionOpinions: defaultPrefs,
            position:Point{
                x:rand::random::<f64>()*SPACE.width as f64,
                y:rand::random::<f64>()*SPACE.height as f64
            }
        };
    }

    fn update(&mut self, skaterPositions:Vec<Point>) -> usize{
        return strategy(self,skaterPositions);
    }

    fn determineReward(&mut self, skaterPositions:Vec<Point>,direction:usize) -> f64{
        let angle = directionChoices[direction].to_radians();
        let newPosition = Point{
            x:(self.position.x + speed * angle.cos() + SPACE.width as f64) % SPACE.width as f64,
            y:(self.position.y + speed * angle.sin() + SPACE.height as f64) % SPACE.height as f64
        };
        let hasCollision = skaterPositions.into_iter().any(
            |p| (p.x - newPosition.x).powi(2) + (p.y - newPosition.y).powi(2) < speed.powi(2)
        );
        if hasCollision{
            return Rewards.collision;
        }
        // now we also move.
        self.position = newPosition;
        return Rewards.avoided;
    }
}

fn onlineLearn(skater:&mut Skater, skaterPositions:Vec<Point>) -> usize{
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
    learn(skater, skaterPositions, chosenIndex);
    return chosenIndex;
}
fn learn(skater:&mut Skater, skaterPositions:Vec<Point>, choice:usize){
    let reward = skater.determineReward(skaterPositions, choice);
    learnStrat(skater, choice, reward);
    skater.actionOpinions[choice].uses += 1;
}

use rand::Rng;
fn egreedy(skater:&mut Skater, skaterPositions:Vec<Point>) -> usize{
    let episolon = 0.1;
    let greed = rand::random::<f64>();
    let choice:usize = if greed <= episolon{
        // explore
        rand::thread_rng().gen_range(0, directionChoices.len())
    }else{
        let start:(usize,f64) = (0, -Rewards.unreasonablyHigh);
        skater.actionOpinions.iter().enumerate().fold(start, |prev,(index,preference)| {
            if prev.1 < preference.lastReward {
                return (index,preference.lastReward);
            }
            return prev;
        }).0
    };
    learn(skater, skaterPositions, choice);
    return choice;
}
fn greedy(skater:&mut Skater, skaterPositions:Vec<Point>) -> usize{
    let start:(usize,f64) = (0, -Rewards.unreasonablyHigh);
    let choice:usize = skater.actionOpinions.iter().enumerate().fold(start, |prev,(index,preference)| {
        if prev.1 < preference.lastReward {
            return (index,preference.lastReward);
        }
        return prev;
    }).0;
    learn(skater, skaterPositions, choice);
    return choice;
}

fn proportional(skater:&mut Skater, skaterPositions:Vec<Point>) -> usize{
    // v = totalReward (of entire game)
    // u gained utility (from playing x)
    // e_x = x been played
    // q_{t-1} = previous propenticy
    return 0;
}

// learn slower and slower
fn regresiveLearning(skater:&mut Skater, action:usize, newReward:f64){
    // oldvalue + learnrate (reward - oldvalue)
    let oldReward = skater.actionOpinions[action].lastReward;
    // where learnrate = (1/n)
    let learnrate= 1.0 / skater.actionOpinions[action].uses as f64; 
    skater.actionOpinions[action].lastReward = oldReward + learnrate * (newReward - oldReward);
}

static learnRate:f64 = 0.2;
fn constantLearning(skater:&mut Skater, action:usize, newReward:f64) {
    // oldvalue + learnrate (reward - oldvalue)
    let oldReward = skater.actionOpinions[action].lastReward;
    // where learnrate = (1/n)
    skater.actionOpinions[action].lastReward = oldReward + learnRate * (newReward - oldReward);
}
use std::fmt;
impl fmt::Display for Skater{
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        let mut commaSeparated = String::new();
        for num in &self.actionOpinions
        {
            commaSeparated.push_str(&num.to_string());
            commaSeparated.push_str(", ");
        }
        commaSeparated.pop();
        commaSeparated.pop();
        return write!(f, "S(x:{} y:{}, {:?})", self.position.x, self.position.y, commaSeparated);
    }
}

impl fmt::Display for Preference{
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        return write!(f, "p(u:{} r:{})", self.uses, self.lastReward);
    }
}
#[derive(Debug)]
struct Step{
    choices:Vec<usize>,
    avgReward:Vec<f64>
}

impl Step{
    fn new() -> Step{
        return Step{
            choices: Vec::<usize>::new(),
            avgReward: Vec::<f64>::new(),
        }
    }
}

// Program
fn main() {
    let mut skaters:Vec<Skater> = (1..skaterCount).into_iter().map(|_| Skater::new()).collect();
    let mut simulationResult = Vec::<Step>::new();
    for _ in 0..simulationCount {
        let positions:Vec<Point> = skaters.clone().into_iter().map(|s| s.position).collect();
        let mut newSkaters = Vec::<Skater>::new();
        let mut stepResult = Step::new();
        let mut prefrences = Vec::<Vec<f64>>::new();
        for mut skater in skaters{
            stepResult.choices.push(skater.update(positions.clone()));
            newSkaters.push(skater.clone());
            prefrences.push(skater.actionOpinions.into_iter().map(|p| p.lastReward).collect());
        }
        // hack to get the right amount of vec<f64> all initialized to 0
        let start:Vec<f64> = directionChoices.into_iter().map(|_| 0.0).collect();
        // we want to remove the outer structure into averages (so its a fold)
        stepResult.avgReward = prefrences.into_iter().fold(start, |prev, cur|{
            // per preference, divide by skatercount and add previous value
            return cur.into_iter().zip(prev.into_iter()).map(|tupple|{
                return tupple.0/skaterCount as f64 + tupple.1
            }).collect();
        });
        simulationResult.push(stepResult);
        skaters = newSkaters;
    }
    plot(simulationResult);
}

fn plot(simulationResult:Vec<Step>){
    let start:Vec<Vec<f64>> = directionChoices.into_iter().map(|_| Vec::<f64>::new()).collect();
    let rewards = simulationResult.iter().fold(start, |mut prev:Vec<Vec<f64>>, cur|{
        for (i,r) in cur.avgReward.iter().enumerate(){
            prev[i].push(r.clone());
        }
        return prev
    });
    fn rngColor() -> String{
        fn rng() -> i32{
            return ( rand::random::<f64>() * 255.0) as i32
        }
        return format!("#{0:x}{1:x}0{2:x}0{3:x}", rng(), rng(), rng(), rng());
    }
    let colors:Vec<String> = directionChoices.into_iter().map(|_| rngColor()).collect();
    drawPlot(rewards, simulationResult.len(), &"rewards", &colors);
    let startu:Vec<Vec<usize>> = directionChoices.into_iter().map(|_| Vec::<usize>::new()).collect();
    let choices = simulationResult.iter().fold(startu, |mut prev:Vec<Vec<usize>>, cur|{
        prev = prev.into_iter().map(|mut x| {
            x.push(0);
            return x
        }).collect();
        for c in cur.choices.clone().into_iter(){
            let mut choice = prev[c].clone();
            let len = choice.len();
            choice[len-1] = choice[len-1] + 1;
            prev[c] = choice;
        }
        return prev;
    });
    drawPlot(choices, simulationResult.len(), &"choices", &colors);
}
fn markerSymbol(nr:usize) -> char{
    let choice = ['.','+','x','*','s','S','o','O','t','T','d','D','r','R'];
    return choice[nr%choice.len()];
}
use gnuplot::{Figure, Caption, Color, DataType, PointSymbol, PointSize};
fn drawPlot<T>(array:Vec<Vec< T >>, stepCount:usize, string:&str, colors:&Vec<String>) where T : DataType{
    let mut fg = Figure::new();
    {
        let mut axis = fg.axes2d();
        for (i,reward) in array.into_iter().enumerate(){
            axis.lines(
                &(0..(stepCount)).collect::<Vec<usize>>(),
                reward,
                &[
                    PointSymbol(markerSymbol(i)),
                    PointSize(30.0),
                    Caption(&format!("{}: {}",string, directionChoices[i])),
                    Color(&colors[i])
                ]
            );
        }
    }
    fg.echo_to_file(&format!("{}.plot", string));
}
