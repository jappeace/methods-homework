use skater::Skater;
use skater::Point;
use learningStrategies::{selectActionFunction,learnFunction};

// Structures
#[derive(Clone)]
pub struct Reward{
    pub collision:f64,
    pub avoided:f64,
    pub unreasonablyHigh:f64
}
#[derive(Clone)]
pub struct Space{
    pub width:i64,
    pub height:i64
}
pub struct RunConfiguration{
    pub runname:String,
    pub simulationCount:i64,
    pub skaterCount:i64,
    pub directionChoices:[f64;6],
    pub speed:f64,
    pub collisionRadius:f64,
    pub space:Space,
    pub rewards:Reward,
    pub plotOptions:PlotOptions,
    pub strategy:selectActionFunction,
    pub learnStrat:learnFunction,
}

pub struct PlotOptions{
    pub shouldPlotChoices:bool,
    pub shouldPlotRewards:bool
}
pub static resultDir:&'static str = "results";

impl RunConfiguration {
    pub fn createWith(runname:String, strategy:selectActionFunction, learnStrat:learnFunction) -> RunConfiguration{
        return RunConfiguration{
            runname:runname,
            simulationCount:3500,
            skaterCount:31,
            directionChoices:[0.0,60.0,120.0,180.0,240.0,300.0],
            speed:0.5,
            collisionRadius:0.5,
            space:Space {
                width: 10,
                height: 10
            },
            rewards:Reward{
                collision:-20.0,
                avoided:20.0,
                unreasonablyHigh:100.0
            },
            plotOptions:PlotOptions{
                shouldPlotChoices:true,
                shouldPlotRewards:true,
            },
            strategy:strategy,
            learnStrat:learnStrat,
        }
    }
}
