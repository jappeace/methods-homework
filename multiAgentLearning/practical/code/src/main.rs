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

extern crate rand;

// Configuration
static simulationCount:i64= 100;
static skaterCount:i64 = 50;
static directionChoices:&'static [i32;12] = &[30,60,90,120,150,180,210,240,270,300,330,0];
static speed:f64 = 1.0;
static collisionRadius:f64 = 1.0;
static SPACE:Space = Space {
    width: 30,
    height: 30
};
static REWARDS:Rewards = Rewards{
    collision:-10.0,
    avoided:10.0
};

// Structures
struct Rewards{
    collision:f64,
    avoided:f64
}
struct Space{
    width:i64,
    height:i64
}
struct Point {
    x: f64,
    y: f64,
}
struct Preference{
    uses:i64,
    lastReward:f64
}
use rand::Rng;
struct Skater{
    actionOpinions:Vec<Preference>, // same size as the action set
    position:Point,
}
impl Skater{

    fn update(&mut self){
        let choice = rand::random::<f64>();
        println!("numbs {}", choice);
    }
    fn learn(&mut self, skaterPositions:Vec<Point>, direction:usize) {
        // oldvalue + learnrate (reward - oldvalue)
        let newReward = Skater::determineReward(skaterPositions, direction);
        let oldReward = self.actionOpinions[direction].lastReward;
        // where learnrate = (1/n)
        let learnrate= 1.0 / self.actionOpinions[direction].uses as f64; 
        self.actionOpinions[direction].lastReward = oldReward + learnrate * (newReward - oldReward);
        self.actionOpinions[direction].uses += 1;
    }
    fn determineReward(skaterPositions:Vec<Point>,direction:usize) -> f64{
        return 0.0;
    }
}

// Program
fn main() {
    let mut skat = Skater{
        actionOpinions: Vec::<Preference>::new(),
        position:Point{x:3.0,y:3.0},
    };
    skat.update();

    println!("hello world {}", SPACE.width);
    for i in 0..simulationCount {
        use std::ops::Rem;
        let select = (i as usize).rem(directionChoices.len());
        println!("wrong spelling {}", directionChoices[select]);
        
    }
}
