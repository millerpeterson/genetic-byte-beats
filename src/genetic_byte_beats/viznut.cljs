(ns genetic-byte-beats.viznut)

(defn yv1f1
  [t]
  (* t (bit-and (bit-or (bit-shift-right t 12)
                        (bit-shift-right t 8))
                63
                (bit-shift-right t 4))))

(defn yv1f2
  [t]
  (* t (bit-shift-right (bit-or (bit-shift-right t 5)
                                (bit-shift-right t 8))
                        (bit-shift-right t 16))))

(defn yv1f3
  [t]
  (* t (bit-and (bit-or (bit-shift-right t 9)
                        (bit-shift-right t 13))
                25
                (bit-shift-right t 6))))

(defn yv1f4
  [t]
  (* t (bit-and (bit-shift-right t 11)
                (bit-shift-right t 8)
                123
                (bit-shift-right t 3))))
