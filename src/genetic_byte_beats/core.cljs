(ns genetic-byte-beats.core
  (:require [genetic-byte-beats.forms.erlehmann :as erlehmann]
            [genetic-byte-beats.gene-ops :as gene-ops]
            [genetic-byte-beats.io :as io]
            [genetic-byte-beats.parsing :as parsing]
            [genetic-byte-beats.forms.evolved :as evolved]
            [reagent.core :as r]))

(enable-console-print!)

(defonce ctx (io/context))
(defonce vol-node (io/volume-node ctx 0.1))
(defonce processor-node (io/script-processor-node ctx 4096 1 1))
(defonce clock (atom 0))
(defonce history (r/atom []))

(defn reset-clock
  "Reset formula clock."
  []
  (reset! clock 0))

(defn volume
  "Adjust volume."
  [v]
  (set! (.-value (.-gain vol-node)) v))

(defn play
  "Start playback for a sample generating function at a given sample rate."
  ([gen-func]
    (play gen-func 8000))
  ([gen-func sample-rate]
   (reset-clock)
   (let [rate-ratio (/ (.-sampleRate ctx) sample-rate)
         event-processor (io/audio-event-processor clock gen-func rate-ratio)]
     (io/configure-node-processor processor-node event-processor)
     (.connect vol-node (.-destination ctx))
     (.connect processor-node vol-node))))

(defn stop
  "Stop playback of the current sample generating function."
  []
  (.disconnect processor-node vol-node)
  (.disconnect vol-node (.-destination ctx)))

(defn play-and-print
  "Play an AST and print it."
  [ast]
  (do
    (play (io/sample-gen-func ast))
    (println ast)))

(defn new-line
  "Create a new cell line, starting with a random formula
  bred from two random parents from a given group of formulas."
  [forms]
  (let [starter (gene-ops/random-child forms)]
    (reset! history (vector starter))))

(defn mutate
  "Add a new cell to the line by mutating the last
  formula then playing it."
  [method]
  (let [mutated (cond (= :complexify method) (gene-ops/mutate-complexify (last @history))
                      (= :simplify method) (gene-ops/mutate-simplify (last @history))
                      :else (gene-ops/mutate-perturb (last @history)))]
    (swap! history #(conj % mutated))
    (play-and-print mutated)))

(defn breed
  [mate-forms]
  "Add a new cell to the line by breeding the last formula with
  a random element from a group of mate formulas, then play it."
  (let [bred (gene-ops/crossover (last @history)
                                 (rand-nth mate-forms))]
    (swap! history #(conj % bred))
    (play-and-print bred)))

(defn undo
  []
  "Remove the last formula from the cell-line, and play the one before it
  (the new last cell)."
  (when-not (empty? @history)
    (swap! history pop)
    (play-and-print (last @history))))

; REPL Playground

(comment
  (reset-clock)
  (volume 0.1)

  (play-and-print (rand-nth evolved/forms))
  (play-and-print (last evolved/forms))
  (play-and-print (last @history))

  (new-line (into erlehmann/forms evolved/forms))
  (breed (into erlehmann/forms evolved/forms))
  (mutate :complexify)
  (mutate :simplify)
  (mutate :perturb)
  (undo)

  (println (last @history))
  (stop)

)

; Web Front-end

(defn play-controls
  []
  [:div
   [:button
    {:on-click #(play-and-print (last @history))}
    "Play"]
   [:button
    {:on-click #(stop)}
    "Stop"]])

(defn genetic-controls
  []
  [:div
   [:button
    {:on-click #(breed (into erlehmann/forms evolved/forms))}
    "Crossover"]
   [:button
    {:on-click #(mutate :perturb)}
    "Mutate"]
   [:button
    {:on-click #(mutate :complexify)}
    "Complexify"]
   [:button
    {:on-click #(mutate :simplify)}
    "Simplify"]])

(defn history-controls
  []
  [:div
   [:button
    {:on-click #(undo)}
    "Undo"]
   [:button
    {:on-click #(new-line (into erlehmann/forms evolved/forms))}
    "New Line"]])

(defn cell-history
  []
  [:ul
   (for [cell-ast (reverse @history)]
     (let [form-string (parsing/string-from-ast cell-ast)]
       ^{:key form-string} [:li form-string]))])

(defn app
  []
  [:div
   [play-controls]
   [genetic-controls]
   [history-controls]
   [cell-history]])

(defn ^:export run []
  (r/render [app]
            (js/document.getElementById "app")))

(defn on-js-reload []
  (run))