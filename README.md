# Genetic Byte Beats

Evolving byte beat algorithms in the browser using genetic programming.

## What's this?

Read my [introductory blog post](http://millerpeterson.github.io/2016/06/03/genetic-byte-beats.html), or
[Viznut's original post](http://countercomplex.blogspot.ca/2011/10/algorithmic-symphonies-from-one-line-of.html)
on the subject.

## Demo

Demo available [here](http://millerpeterson.github.io/byte-beats/)
(your browser must support the WebAudio API). Click the "New Line" button to get started.

The following functions are available in the demo:

* New Line - start evolving formulas by randomly creating a starting point formula.
* Play / Stop - play or stop browser audio.
* Crossover - randomly breed the current formula with another one.
* Mutate - make a small adjustment to a number in the current formula.
* Simplify - simplify a formula sub-expression by replacing it with a constant.
* Complexify - make a formula more complex by replacing a constant with a sub-expression.
* Undo - undo the last operation.