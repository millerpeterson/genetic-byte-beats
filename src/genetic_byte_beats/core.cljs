(ns genetic-byte-beats.core
  (:require [genetic-byte-beats.viznut :as viznut]))

(enable-console-print!)

(defn context
  []
  (js/AudioContext.))

(defn script-processor-node
  [ctx buff-size num-input-chan num-output-chan]
  (.createScriptProcessor ctx buff-size num-input-chan num-output-chan))

(defonce ctx (context))
(defonce node (script-processor-node ctx 4096 1 1))
(defonce clock (atom 0))

(defn disconnect-output
  [node]
  (.disconnect node (.-destination ctx)))

(defn connect-output
  [node]
  (.connect node (.-destination ctx)))

(defn configure-node-processor
  [node processor-fn]
  (set! (.-onaudioprocess node) processor-fn))

(defn reset-clock!
  []
  (reset! clock 0))

(defn folded-amp
  [val]
  "Quantized input value between 0-255, scaled to -1.0 - 1.0."
  (let [folded (mod val 255)]
    (- (* 2 (/ folded 255.0)) 1)))

(defn fill-buffer!
  [out-buff buffer-sample-gen]
  "Fill a sample buffer with a given generator. The generator should accept one argument
   for the buffer index."
  (let [buff-size (.-length out-buff)
        buff-positions (range 0 buff-size)
        samples (.getChannelData out-buff 0)]
    (doseq [buff-index buff-positions]
      (aset samples buff-index (buffer-sample-gen buff-index)))))

(defn processor
  [sample-gen clock-ref ap-event]
  "Function that fills the audio buffer in an autioprocess event with samples from using
   sample-gen ranging over a clock's values."
  (let [out-buff (.-outputBuffer ap-event)
        buffer-sample-gen (fn [buff-index] (sample-gen (+ buff-index (deref clock-ref))))]
    (fill-buffer! out-buff (comp folded-amp buffer-sample-gen))
    (swap! clock-ref #(+ % (.-length out-buff)))))

(defn play
  [gen-func]
  (reset-clock!)
  (let [event-processor (partial processor gen-func clock)]
    (configure-node-processor node event-processor)
    (connect-output node)))

(defn stop
  []
  (disconnect-output node))

(comment
  (play viznut/yv1f1)
  (play viznut/yv1f2)
  (play viznut/yv1f3)
  (play viznut/yv1f4)
  (stop))

(defn on-js-reload [])