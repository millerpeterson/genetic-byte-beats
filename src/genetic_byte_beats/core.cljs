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
  [gen-ast]
  (let [func-def (cons 'fn (cons '[t] (list gen-ast)))]
    (eval (empty-state)
          func-def
          {:eval js-eval
           :source-map true
           :context :expr}
          identity)))

(defn play
  [gen-func sample-rate]
  (reset-clock!)
  (let [rate-ratio (/ (.-sampleRate ctx) sample-rate)
        event-processor (io/audio-event-processor clock gen-func rate-ratio)]
    (io/configure-node-processor processor-node event-processor)
    (.connect vol-node (.-destination ctx))
    (.connect processor-node vol-node)))

(defn stop
  []
  (.disconnect processor-node vol-node)
  (.disconnect vol-node (.-destination ctx)))

(defn volume
  [v]
  (set! (.-value (.-gain vol-node)) v))

(defn reset-clock!
  []
  (reset! clock 0))

(comment
  (play (sample-gen-func viznut/yv1f1) 8000)
  (play (sample-gen-func viznut/yv1f2) 8000)
  (play (sample-gen-func viznut/yv1f3) 8000)
  (play (sample-gen-func viznut/yv1f4) 8000)
  (play (sample-gen-func viznut/yv1f5) 8000)
  (play (sample-gen-func viznut/yv1f6) 8000)
  (play (sample-gen-func viznut/yv1f7) 8000)
  (play (sample-gen-func (first (gene-ops/random-children viznut/forms))) 8000)
  (stop))

(defn on-js-reload [])