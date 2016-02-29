(ns genetic-byte-beats.forms.evolved)

(def forms
  ['(bit-xor (bit-shift-right (/ t 6) 11) (bit-and (bit-shift-right (/ t 6) 8) (* (bit-shift-right (/ t 6) (bit-or (bit-shift-right (/ t 18) 4) (bit-shift-left t 3))) t)))
   '(bit-or (bit-shift-right (bit-shift-right t (bit-and (bit-shift-right (/ t 12) 9) (* t t))) (bit-shift-right t 18)) (/ t 1000))

   ])