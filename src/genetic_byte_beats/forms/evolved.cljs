(ns genetic-byte-beats.forms.evolved)

(def forms
  ['(bit-xor (bit-shift-right (/ t 6) 11) (bit-and (bit-shift-right (/ t 6) 8) (* (bit-shift-right (/ t 6) (bit-or (bit-shift-right (/ t 18) 4) (bit-shift-left t 3))) t)))
   '(bit-or (bit-shift-right (bit-shift-right t (bit-and (bit-shift-right (/ t 12) 9) (* t t))) (bit-shift-right t 18)) (/ t 1000))
   '(bit-xor (bit-shift-right (* t (bit-shift-right 8 (int (* 3 (js/Math.sin (bit-and (* t 3) (int (/ t 329)))))))) (bit-and t 691)) (bit-shift-right (bit-and t 512) (int (* 5 (js/Math.sin (bit-and (* t 4) (int (/ t 753))))))))
   '(bit-or (bit-and (* t 6) (bit-or (bit-shift-right (bit-and t 512) (bit-and 7 (bit-shift-right t 12))) (/ t 127))) (bit-and (* t 4) (* (bit-shift-right t 6) 6)))
   '(bit-or t (bit-shift-right (bit-and t t) t))])