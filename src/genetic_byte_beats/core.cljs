(ns genetic-byte-beats.core
  (:require [genetic-byte-beats.forms.erlehmann :as erlehmann]
            [genetic-byte-beats.gene-ops :as gene-ops]
            [genetic-byte-beats.io :as io]))

(enable-console-print!)

(defonce ctx (io/context))
(defonce vol-node (io/volume-node ctx 0.1))
(defonce processor-node (io/script-processor-node ctx 4096 1 1))
(defonce clock (atom 0))
(defonce history (atom []))

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

(defn new-line
  "Create a new cell line, starting with a random formula
  from a given group of formulas."
  [forms]
  (do
    (swap! history #(vector (rand-nth forms)))
    (play (io/sample-gen-func (first @history)))
    (println (first @history))))

(defn mutate
  "Mutate the last formula."
  []
  (let [mutated (gene-ops/mutate (last @history))]
    (swap! history #(conj % mutated))
    (play (io/sample-gen-func mutated))
    (println mutated)))

(defn breed
  [mate-forms]
  "Breed the last formula with a random element from a group of mate
  formulas."
  (let [bred (gene-ops/crossover (last @history)
                                 (rand-nth mate-forms))]
    (swap! history #(conj % bred))
    (play (io/sample-gen-func bred))
    (println bred)))

(comment

  (reset-clock)
  (volume 0.1)

  (new-line erlehmann/forms)
  (breed erlehmann/forms)
  (mutate)

  (println (last @history))

  (stop)
)

(defn on-js-reload [])