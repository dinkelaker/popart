///////////////////////////////////////////////////////////////////////////////
// Copyright 2008-2015, Technische Universitaet Darmstadt (TUD), Germany
//
// The TUD licenses this file to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
///////////////////////////////////////////////////////////////////////////////
package de.tud.stg.tests.popart.extensions.cool.shape;

import java.util.Vector;
import java.util.Random;

// Shape example from Lopez Diss.
public class MyShape {
  
  protected double x_= 0.0, y_= 0.0;
  protected double width_=0.0, height_=0.0;
  private Random mRandom = new Random();
  
  double x() { return x_; }
  double y() { return y_; }
  double width(){ return width_; }
  double height(){ return height_; }
  
  void adjustLocation() throws Throwable {
    x_ = longCalculation();
    y_ = longCalculation();
  }
  
  void adjustDimensions() throws Throwable {
    width_ = longCalculation();
    height_ = longCalculation();
  }
  
  private double longCalculation() throws Throwable {
    double v = mRandom.nextDouble();
    Thread.currentThread().sleep((int)(v*2000.0));
    return v;
  }
}
