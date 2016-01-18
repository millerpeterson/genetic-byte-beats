(ns genetic-byte-beats.core)

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

(defn sample-gen
  [t]
  (let [byte (mod (bit-and t (bit-shift-right t 2)) 255)]
    (- (* 2 (/ byte 255.0)) 1)))

(defn processor
  [ap-event]
  (let [out-buff (.-outputBuffer ap-event)
        buff-size (.-length out-buff)
        buff-positions (range 0 buff-size)
        samples (.getChannelData out-buff 0)]
    (doseq [s buff-positions]
      (aset samples s (sample-gen (+ @clock s))))
    (swap! clock #(+ % buff-size))))

(comment
  (connect-output node)
  (configure-node-processor node processor))

(defn on-js-reload []
  (disconnect-output node)
  (reset-clock!)
  (configure-node-processor node processor)
  (connect-output node))