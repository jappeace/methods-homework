use config::{Reward, Space};
use learningStrategies::{selectActionFunction, learnFunction};

#[derive(Clone)]
#[derive(Debug)]
pub struct Point {
    x: f64,
    y: f64,
}
#[derive(Clone)]
#[derive(Debug)]
pub struct Preference{
pub   uses:i64,
pub   lastReward:f64
}
#[derive(Clone)]
pub struct Skater{
pub   actionOpinions:Vec<Preference>, // same size as the action set
pub   position:Point,
pub   availableActions:Vec<f64>,
pub   rewards:Reward,
pub   space:Space,
pub   speed:f64,
pub   radius:f64
}

use rand::random;
use config::RunConfiguration;
impl Skater{

    pub fn new(config:&RunConfiguration) -> Skater{
        let defaultPrefs = config.directionChoices.into_iter().map( |_|
            Preference{
                uses:1,
                lastReward:config.rewards.unreasonablyHigh
            }
        ).collect();
        return Skater{
            actionOpinions: defaultPrefs,
            position:Point{
                x:random::<f64>()*config.space.width as f64,
                y:random::<f64>()*config.space.height as f64
            },
            availableActions: config.directionChoices.iter().cloned().collect(),
            rewards:config.rewards.clone(),
            space:config.space.clone(),
            speed:config.speed,
            radius:config.collisionRadius
        };
    }

    pub fn update(&mut self, skaterPositions:Vec<Point>, learnF:&learnFunction,
              strategy:&selectActionFunction) -> usize{
        return strategy(learnF, self,skaterPositions);
    }

    pub fn determineReward(&mut self, skaterPositions:Vec<Point>,direction:usize) -> f64{
        let angle = self.availableActions[direction].to_radians();
        let newPosition = Point{
            x:(self.position.x + self.speed * angle.cos() + self.space.width as f64)
                % self.space.width as f64,
            y:(self.position.y + self.speed * angle.sin() + self.space.height as f64)
                % self.space.height as f64
        };
        let hasCollision = skaterPositions.into_iter().filter(
            |x| self.position.x != x.x && self.position.y != x.y
        ).any(
            |p| (p.x - newPosition.x).powi(2) + (p.y - newPosition.y).powi(2) < self.radius.powi(2)
        );
        if hasCollision{
            return self.rewards.collision;
        }
        // now we also move.
        self.position = newPosition;
        return self.rewards.avoided;
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
