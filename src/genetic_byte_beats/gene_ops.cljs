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

(defn list-locs
  "All sub-trees in the operator tree of a given formula."
  [formula]
  (filter (comp list? zip/node) (op-tree-locs formula)))

(defn replace-branch
  "Replace a formula branch rooted at l with a formula branch rooted at r."
  [l r]
  (zip/root (zip/replace l (zip/node r))))

(defn mutate-perturb
  "Randomly modify a random number in a formula up to a max percent."
  [formula]
   (zip/root (zip/edit (rand-nth (constant-locs formula)) #(rand-int 100))))

(defn rand-opped-node
  "A random AST node that is a function of a formula node."
  [node]
  (let [rand-val (rand-int 100)]
    (rand-nth (vec [`(~'bit-shift-right ~'t ~node)
                    `(~'bit-shift-right ~node ~rand-val)
                    `(~'bit-shift-left ~'t ~node)
                    `(~'bit-shift-left ~node ~rand-val)
                    `(~'* ~'t ~node)
                    `(~'* ~node ~rand-val)
                    `(~'% ~'t ~node)
                    `(~'% ~node ~rand-val)
                    `(~'bit-or ~'t ~node)
                    `(~'bit-or ~'t ~rand-val)
                    `(~'bit-xor ~'t ~node)
                    `(~'bit-xor ~node ~rand-val)
                    `(~'bit-and ~'t ~node)
                    `(~'bit-and ~node ~rand-val)
                    `(~'js/Math.sin ~node)
                    `(~'js/Math.tan ~node)]))))

(defn mutate-complexify
  "Mutate a formula by randomly replace one of its sub-trees with a
   function involving that subtree and t."
  [formula]
  (let [target-node (rand-nth (op-tree-locs formula))]
    (zip/root (zip/edit target-node rand-opped-node))))

(defn mutate-simplify
  "Mutate a formula by randomly replace one of its sub-trees with a
   constant."
  [formula]
  (let [target-node (rand-nth (list-locs formula))]
    (zip/root (zip/edit target-node #(rand-int 100)))))

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
  (mutate-perturb (crossover (rand-nth forms) (rand-nth forms))))
