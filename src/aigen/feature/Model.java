package aigen.feature;

//Model class
class Model extends Feature {
 private int number;

 public Model(int number) {
     super();
     this.number = number;
     this.levelCode = "M";
 }

 @Override
 public String toString() {
     return String.valueOf(number);
 }
}
