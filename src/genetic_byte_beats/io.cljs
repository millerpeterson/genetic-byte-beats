(ns genetic-byte-beats.io)

(defn fill-buffer!
  [out-buff buffer-sample-gen]
  "Fill a sample buffer with a given generator. The generator should accept one argument
   for the buffer index."
  (let [buff-size (.-length out-buff)
        buff-positions (range 0 buff-size)
        samples (.getChannelData out-buff 0)]
    (doseq [buff-index buff-positions]
      (aset samples buff-index (buffer-sample-gen buff-index)))))

(defn folded-amp
  [val]
  "Quantized input value between 0-255, scaled to -1.0 - 1.0."
  (let [folded (mod val 255)]
    (- (* 2 (/ folded 255.0)) 1)))

(defn audio-event-processor
  [clock-ref sample-gen rate-ratio]
  "A function that fills the audio buffer in an autioprocess event with samples from using
   sample-gen ranging over a clock's values, rate adjusted according to a sample rate ratio."
  (fn [ap-event]
    (let [out-buff (.-outputBuffer ap-event)
          buffer-sample-gen (fn [buff-index]
                              (let [clock-rel-t (+ buff-index (deref clock-ref))
                                    rate-adjusted-t (Math/floor (/ clock-rel-t rate-ratio))]
                                (sample-gen rate-adjusted-t)))]
      (fill-buffer! out-buff (comp folded-amp buffer-sample-gen))
      (swap! clock-ref #(+ % (.-length out-buff))))))

(defn context
  "Create a web audio context."
  []
  (js/AudioContext.))

(defn script-processor-node
  [ctx buff-size num-input-chan num-output-chan]
  "Create a web audio script processor node."
  (.createScriptProcessor ctx buff-size num-input-chan num-output-chan))

(defn configure-node-processor
  [node processor-fn]
  "Configure a script processor node to use an audioprocess event handler."
  (set! (.-onaudioprocess node) processor-fn)
  node)

(defn volume-node
  [ctx start-gain]
  "A web audio gain control node."
  (let [node (.createGain ctx)]
    (set! (.-value (.-gain node)) start-gain)
    node))