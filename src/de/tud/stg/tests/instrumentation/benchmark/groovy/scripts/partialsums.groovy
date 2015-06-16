package de.tud.stg.tests.instrumentation.benchmark.groovy.scripts

import java.text.DecimalFormat

// ---------------------------------------------------------------------
// The Great Computer Language Shootout
// http://shootout.alioth.debian.org/
//
// Use JRE 1.4 features only [JRE 1.5 'printf' would have helped better
// streamline code]
//
// Contributed by Anthony Borla
// Modified by Alex Tkachman
// ---------------------------------------------------------------------

def calculate()
{
    final double twothirds = 2.0d/3.0d;

    n = Integer.parseInt(args[0])

    double a1 = 0.0d, a2 = 0.0d, a3 = 0.0d, a4 = 0.0d, a5 = 0.0d
    double a6 = 0.0d, a7 = 0.0d, a8 = 0.0d, a9 = 0.0d, alt = -1.0d

    double k = 1.0d
    while (k<=n){
       double k2 = Math.pow(k,2.0d), k3 = k2*k
       double sk = Math.sin(k), ck = Math.cos(k)
       alt = -alt

       a1 = a1 + Math.pow(twothirds,k-1.0d)
       a2 = a2 + Math.pow(k,-0.5d)
       a3 = a3 + 1.0d/(k*(k+1.0d))
       a4 = a4 + 1.0d/(k3 * sk*sk)
       a5 = a5 + 1.0d/(k3 * ck*ck)
       a6 = a6 + 1.0d/k
       a7 = a7 + 1.0d/k2
       a8 = a8 + alt/k
       a9 = a9 + alt/(2.0d*k-1.0d)
       k += 1.0d
    }

    def fmt = new DecimalFormat("##0.000000000")

    result = fmt.format(a1); println "${result}\t(2/3)^k"
    result = fmt.format(a2); println "${result}\tk^-0.5"
    result = fmt.format(a3); println "${result}\t1/k(k+1)"
    result = fmt.format(a4); println "${result}\tFlint Hills"
    result = fmt.format(a5); println "${result}\tCookson Hills"
    result = fmt.format(a6); println "${result}\tHarmonic"
    result = fmt.format(a7); println "${result}\tRiemann Zeta"
    result = fmt.format(a8); println "${result}\tAlternating Harmonic"
    result = fmt.format(a9); println "${result}\tGregory"
}

// --------------------------------

long start = System.currentTimeMillis ()
calculate()
println "${System.currentTimeMillis () - start}ms"

