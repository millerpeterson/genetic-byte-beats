(ns genetic-byte-beats.gene-ops
  (:require [clojure.zip :as zip]))

; General approach and code snippets taken from:
; http://www.thattommyhall.com/2013/08/23/genetic-programming-in-clojure-with-zippers/

(def gene-fns
  "Symbols that are functions that may appear in the formulas."
  #{'* '/ '+ '- 'mod
    'bit-and 'bit-or 'bit-xor
    'bit-shift-right 'bit-shift-left
    'js/Math.sin 'js/Math.tan 'int})

(defn op-tree-locs
  "All valid locations in the operator tree of a given formula."
  [formula]
  (let [zipper (zip/seq-zip formula)
        all-locs (take-while (complement zip/end?) (iterate zip/next zipper))]
    (filter #(not (gene-fns (zip/node %))) all-locs)))

(defn constant-locs
  "All constants in the operator tree of a given formula."
  [formula]
  (filter (comp number? zip/node) (op-tree-locs formula)))

(defn mutate
  "Randomly modify a random number in a formula up to a max percent."
  ([formula]
   (mutate formula 0.5))
  ([formula max-percent]
   (zip/root (zip/edit (rand-nth (constant-locs formula))
                       (comp int (partial * (+ 1
                                               (- (* (rand) 2 max-percent) max-percent))))))))

(defn replace-branch
  "Replace a formula branch rooted at l with a formula branch rooted at r."
  [l r]
  (zip/root (zip/replace l (zip/node r))))

(defn crossover
  "Return a breed two formula, switching a random branch in l
   with a random branch in r."
  [l-form r-form]
  (let [l (rand-nth (op-tree-locs l-form))
        r (rand-nth (op-tree-locs r-form))]
    (replace-branch l r)))

(defn random-child
  "Return a child formula resulting from crossing over two randomly chosen
   formula from forms, then mutating it."
  [forms]
  (mutate (crossover (rand-nth forms) (rand-nth forms))))
