package app;

public class ComplexNumber {
    // data
    public float real;
    public float imag;

    // set value
    public void value(float real, float imag) {
        this.real = real;
        this.imag = imag;
    }

    // get value
    public String value() {
        String result = "" + this.real;
        if(this.imag > 0)
            result += "+";
        return result + this.imag + "i";
    }

    // clone
    public ComplexNumber clone() {
        ComplexNumber result = new ComplexNumber();
        result.real = this.real;
        result.imag = this.imag;
        return result;
    }

    // add
    public static ComplexNumber add(ComplexNumber a, ComplexNumber b) {
        ComplexNumber result = new ComplexNumber();
        result.real = a.real + b.real;
        result.imag = a.imag + b.imag;
        return result;
    }

    // multiply
    public static ComplexNumber multiply(ComplexNumber a, ComplexNumber b) {
        ComplexNumber result = new ComplexNumber();
        result.real = a.real*b.real - a.imag*b.imag;
        result.imag = a.real*b.imag + a.imag*b.real;
        return result;
    }

    // magnitude
    public float magnitude() {
        return this.real*this.real + this.imag*this.imag;
    }
}