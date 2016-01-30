(ns genetic-byte-beats.core
  (:require [genetic-byte-beats.viznut :as viznut]
            [genetic-byte-beats.io :as io]))

(enable-console-print!)

(defonce ctx (io/context))
(defonce vol-node (io/volume-node ctx 0.1))
(defonce processor-node (io/script-processor-node ctx 4096 1 1))
(defonce clock (atom 0))

(defn reset-clock!
  []
  (reset! clock 0))

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

(comment
  (play viznut/yv1f1 8000)
  (play viznut/yv1f2 8000)
  (play viznut/yv1f3 8000)
  (play viznut/yv1f4 8000)
  (play viznut/yv1f5 8000)
  (stop))

(defn on-js-reload [])