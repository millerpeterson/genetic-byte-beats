(ns genetic-byte-beats.gene-ops
  (:require [clojure.zip :as zip]))

(def gene-fns #{'* '+ 'bit-and 'bit-or 'bit-shift-right})

(defn branch-locs
  [genes]
  (let [zipper (zip/seq-zip genes)
        all-locs (take-while (complement zip/end?) (iterate zip/next zipper))]
    (filter #(not (gene-fns (zip/node %))) all-locs)))

(defn replace-branch
  [l r]
  (zip/root (zip/replace l (zip/node r))))

(defn breed
  [l-genes r-genes]
  (let [l (rand-nth (branch-locs l-genes))
        r (rand-nth (branch-locs r-genes))]
    [(replace-branch l r) (replace-branch r l)]))

(defn random-children
  [forms]
  (breed (rand-nth forms) (rand-nth forms)))
