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
  [amp]
  (let [folded (mod amp 255)]
    (- (* 2 (/ folded 255.0)) 1)))

(defn sample-gen
  [t gen-func]
  (folded-amp (gen-func t)))

(defn fill-buffer!
  [out-buff fill-func]
  (let [buff-size (.-length out-buff)
        buff-positions (range 0 buff-size)
        samples (.getChannelData out-buff 0)]
    (doseq [s buff-positions]
      (aset samples s (fill-func s)))))

(defn processor
  [gen-func ap-event]
  (let [out-buff (.-outputBuffer ap-event)]
    (fill-buffer! out-buff
                  #(sample-gen (+ @clock %) gen-func))
    (swap! clock #(+ % (.-length out-buff)))))

(defn play
  [gen-func]
  (reset-clock!)
  (configure-node-processor node (partial processor gen-func))
  (connect-output node))

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