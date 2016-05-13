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

// Configuration
static simulationCount:i64= 1000;
static skaterCount:i64 = 5;
static directionChoices:&'static [f64;12] = &[0.0,30.0,60.0,90.0,120.0,150.0,180.0,210.0,240.0,270.0,300.0,330.0];
static speed:f64 = 1.0;
static collisionRadius:f64 = 1.0;
static SPACE:Space = Space {
    width: 30,
    height: 30
};
static Rewards:Reward = Reward{
    collision:1.0,
    avoided:10.0,
    unreasonablyHigh:100.0

};

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
#[derive(Debug)]
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
            },
        };
    }
    fn update(mut self, skaterPositions:Vec<Point>) -> Skater{
        let rewards:Vec<f64> = self.actionOpinions.clone().into_iter().map(
            |opinion:Preference| opinion.lastReward
        ).collect();
        let totalReward = rewards.clone().into_iter().fold(0.0,|cur,prev| cur+prev);
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
        self.learn(skaterPositions, chosenIndex);
        return self;
    }

    fn learn(&mut self, skaterPositions:Vec<Point>, direction:usize) {
        // oldvalue + learnrate (reward - oldvalue)
        let newReward = self.determineReward(skaterPositions, direction);
        let oldReward = self.actionOpinions[direction].lastReward;
        // where learnrate = (1/n)
        let learnrate= 1.0 / self.actionOpinions[direction].uses as f64; 
        println!("newReward: {}, oldReward: {}, learnrate {}", newReward, oldReward, learnrate);
        self.actionOpinions[direction].lastReward = oldReward + learnrate * (newReward - oldReward);
        self.actionOpinions[direction].uses += 1;
    }

    fn determineReward(&mut self, skaterPositions:Vec<Point>,direction:usize) -> f64{
        let angle = directionChoices[direction];
        let newPosition = Point{
            x:self.position.x + speed * angle.cos(),
            y:self.position.y + speed * angle.sin()
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

// Program
fn main() {
    let mut skaters:Vec<Skater> = (1..skaterCount).into_iter().map(|_| Skater::new()).collect();
    for _ in 0..simulationCount {
        let positions:Vec<Point> = skaters.clone().into_iter().map(|s| s.position).collect();
        skaters = skaters.into_iter().map(|s| s.update(positions.clone())).collect();

        for skater in skaters.clone(){
            println!("S:{}", skater);
        }
    }
}
