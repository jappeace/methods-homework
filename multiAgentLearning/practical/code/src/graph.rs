use config::RunConfiguration;
use gnuplot::*;
#[derive(Debug)]
pub struct Step{
    pub choices:Vec<usize>,
    pub avgReward:Vec<f64>
}

impl Step{
    pub fn new() -> Step{
        return Step{
            choices: Vec::<usize>::new(),
            avgReward: Vec::<f64>::new(),
        }
    }
}
pub struct PlotDrawer{
    config:RunConfiguration
}
impl PlotDrawer{
    pub fn new(config:RunConfiguration) -> PlotDrawer{
        return PlotDrawer{
            config: config
        }
    }
    pub fn plot(&self, simulationResult:Vec<Step>){
        let start:Vec<Vec<f64>> = self.config.directionChoices.into_iter().map(|_| Vec::<f64>::new()).collect();
        let rewards = simulationResult.iter().fold(start, |mut prev:Vec<Vec<f64>>, cur|{
            for (i,r) in cur.avgReward.iter().enumerate(){
                prev[i].push(r.clone());
            }
            return prev
        });
        self.drawPlot(rewards, simulationResult.len(), &"rewards");
        let startu:Vec<Vec<usize>> = self.config.directionChoices.into_iter().map(|_| Vec::<usize>::new()).collect();
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
        self.drawPlot(choices, simulationResult.len(), &"choices");
    }
    fn drawPlot<T>(&self, array:Vec<Vec< T >>, stepCount:usize, string:&str) where T : DataType{
        fn markerSymbol(nr:usize) -> char{
            let choice = ['.','+','x','*','s','S','o','O','t','T','d','D','r','R'];
            return choice[nr%choice.len()];
        }
        fn colors(nr:usize) -> String {
            let choice = ["red", "green", "blue", "orange", "black", "cyan"];
            return choice[nr%choice.len()].to_string();
        }
        let mut fg = Figure::new();
        {
            let mut axis = fg.axes2d();
            for (i,reward) in array.into_iter().enumerate(){
                axis.lines_points(
                    &(0..(stepCount)).collect::<Vec<usize>>(),
                    reward,
                    &[
                        PointSymbol(markerSymbol(i)),
                        PointSize(0.5),
                        Caption(&format!("{}: {}",string, self.config.directionChoices[i])),
                        Color(&colors(i)),
                        LineWidth(1.0)

                    ]
                );
            }
        }
        let plotname = format!("{}-{}", self.config.runname, string);
        println!("writing {}", plotname);
        use config::resultDir;
        fg.echo_to_file(&format!("{}/{}.plot", resultDir, plotname));
    }
}
