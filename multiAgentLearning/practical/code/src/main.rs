// This programs simulates a primitive reinforcement learning scanerio
// Copyright (C) 2016 Jappe Klooster

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


static simulationCount:i64= 100;
struct Space{
    width:i64,
    height:i64
}
static SPACE:Space = Space {
    width: 30,
    height: 30
};
static directionChoices:&'static [i32;12] = &[30,60,90,120,150,180,210,240,270,300,330,0];
fn main () {
    println!("hello world {}", SPACE.width);
    for i in 0..simulationCount {
        use std::ops::Rem;
        let select = (i as usize).rem(directionChoices.len());
        println!("wrong spelling {}", directionChoices[select]);
        
    }
}
