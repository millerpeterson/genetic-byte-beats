(ns genetic-byte-beats.core
  (:require [genetic-byte-beats.viznut :as viznut]
            [genetic-byte-beats.gene-ops :as gene-ops]
            [genetic-byte-beats.io :as io]
            [cljs.js :refer [empty-state eval js-eval]]))

(enable-console-print!)

(defonce ctx (io/context))
(defonce vol-node (io/volume-node ctx 0.1))
(defonce processor-node (io/script-processor-node ctx 4096 1 1))
(defonce clock (atom 0))

(defn sample-gen-func
  "Return a function for generating sample values from the AST of a gen formula."
  [gen-ast]
  (let [func-def (cons 'fn (cons '[t] (list gen-ast)))]
    (eval (empty-state)
          func-def
          {:eval js-eval
           :source-map true
           :context :expr}
          identity)))

(defn play
  "Start playback for a sample generating function at a given sample rate."
  ([gen-func]
    (play gen-func 8000))
  ([gen-func sample-rate]
   (reset-clock!)
   (let [rate-ratio (/ (.-sampleRate ctx) sample-rate)
         event-processor (io/audio-event-processor clock gen-func rate-ratio)]
     (io/configure-node-processor processor-node event-processor)
     (.connect vol-node (.-destination ctx))
     (.connect processor-node vol-node))))

(defn stop
  "Stop playback of the current sample generating functino."
  []
  (.disconnect processor-node vol-node)
  (.disconnect vol-node (.-destination ctx)))

(defn volume
  "Adjust volume."
  [v]
  (set! (.-value (.-gain vol-node)) v))

(defn reset-clock!
  "Reset formula clock."
  []
  (reset! clock 0))

(comment
  (reset-clock!)
  (volume 0.01)

  ; Formulas from Viznut's video.
  (play (sample-gen-func viznut/yv1f1))
  (play (sample-gen-func viznut/yv1f2))
  (play (sample-gen-func viznut/yv1f3))
  (play (sample-gen-func viznut/yv1f4))
  (play (sample-gen-func viznut/yv1f5))
  (play (sample-gen-func viznut/yv1f6))
  (play (sample-gen-func viznut/yv1f7))
  (stop)

  ; Breeding Viznut's 1st and 4th YouTube formulas.
  (play (sample-gen-func viznut/yv1f1))
  (stop)
  (play (sample-gen-func viznut/yv1f4))
  (stop)
  (let [offspring (gene-ops/crossover viznut/yv1f1
                                      viznut/yv1f4)]
    (println offspring)
    (play (sample-gen-func offspring)))
  (stop)

  ; Breeding Viznut's 2nd and 7th YouTube formulas.
  (play (sample-gen-func viznut/yv1f2))
  (stop)
  (play (sample-gen-func viznut/yv1f7))
  (stop)
  (let [offspring (gene-ops/crossover viznut/yv1f2
                                      viznut/yv1f7)]
    (println offspring)
    (play (sample-gen-func offspring)))
  (stop)

  ; Random breeding of Viznut's formulas.
  (play (sample-gen-func (gene-ops/random-child viznut/forms)) 8000))

(defn on-js-reload [])