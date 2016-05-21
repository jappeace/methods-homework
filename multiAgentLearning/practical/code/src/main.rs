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

#![allow(non_upper_case_globals)]
#![allow(non_snake_case)]

extern crate gnuplot;
extern crate rand;


mod skater;
mod config;
mod learningStrategies;
mod graph;
use skater::{Skater, Point};
use config::{RunConfiguration, resultDir};
use graph::{Step, PlotDrawer};
use learningStrategies::*;
use std::thread;
use std::error::Error;
// Program
fn main() {
    // reset output
    match createOutpuDir() {
        Ok(s) => println!("output dir reset"),
        Err(e) => {
            println!("could not create ouptutdir {}", e.to_string());
            return
        },
    }

    // create configs
    let cfgs = vec!(RunConfiguration::createWith(
        "egreedy".to_string(), learningStrategies::egreedy, learningStrategies::constantLearning
    ),RunConfiguration::createWith(
        "greedy".to_string(), learningStrategies::egreedy, learningStrategies::constantLearning
    ),RunConfiguration::createWith(
        "mixed".to_string(), learningStrategies::egreedy, learningStrategies::constantLearning
    ),RunConfiguration::createWith(
        "egreedy-reg".to_string(), learningStrategies::egreedy, learningStrategies::regresiveLearning
    ),RunConfiguration::createWith(
        "greedy-reg".to_string(), learningStrategies::egreedy, learningStrategies::regresiveLearning
    ),RunConfiguration::createWith(
        "mixed-reg".to_string(), learningStrategies::egreedy, learningStrategies::regresiveLearning
    )
    );

    // map the configurations into tasks (which get executed on seperate threads)
    use std::thread::JoinHandle;
    let mut handl:Vec<JoinHandle<()>> = vec![];
    handl.extend(cfgs.into_iter().map(|cfg| thread::spawn(|| run(cfg))));

    let skaterCountRange:Vec<i64> = vec![20,25,30,35,40];
    handl.extend( skaterCountRange.into_iter().map(|count|{
        let name = format!("egreedy-{}", count);
        let mut config = RunConfiguration::createWith(
            name, learningStrategies::egreedy, learningStrategies::constantLearning
        );
        config.skaterCount = count;
        thread::spawn(|| {
            run(config);
        })
    }));

    // wait for the tasks to complete
    for h in handl{
        h.join();
    }
}
// a single simulation run based on the configuration
fn run(config:RunConfiguration){
    let mut skaters:Vec<Skater> = (1..config.skaterCount).into_iter().map(
        |_| Skater::new(&config)).collect();
    let mut simulationResult = Vec::<Step>::new();
    for _ in 0..config.simulationCount {
        let positions:Vec<Point> = skaters.clone().into_iter().map(|s| s.position).collect();
        let mut newSkaters = Vec::<Skater>::new();
        let mut stepResult = Step::new();
        let mut prefrences = Vec::<Vec<f64>>::new();
        for mut skater in skaters{
            stepResult.choices.push(skater.update(positions.clone(), &config.learnStrat, &config.strategy));
            newSkaters.push(skater.clone());
            prefrences.push(skater.actionOpinions.into_iter().map(|p| p.lastReward).collect());
        }
        // hack to get the right amount of vec<f64> all initialized to 0
        let start:Vec<f64> = config.directionChoices.into_iter().map(|_| 0.0).collect();
        // we want to remove the outer structure into averages (so its a fold)
        stepResult.avgReward = prefrences.into_iter().fold(start, |prev, cur|{
            // per preference, divide by skatercount and add previous value
            return cur.into_iter().zip(prev.into_iter()).map(|tupple|{
                return tupple.0/config.skaterCount as f64 + tupple.1
            }).collect();
        });
        simulationResult.push(stepResult);
        skaters = newSkaters;
    }
    PlotDrawer::new(config).plot(simulationResult);
}

fn createOutpuDir() -> Result<String, Box<Error>>{
    use std::fs;
    try!(fs::remove_dir_all(resultDir));
    try!(fs::create_dir(resultDir));
    Ok("Done".into())
}
