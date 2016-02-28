(ns genetic-byte-beats.forms.viznut)

(def yv1f1
  '(* t (bit-and (bit-or (bit-shift-right t 12)
                         (bit-shift-right t 8))
                 63
                 (bit-shift-right t 4))))

(def yv1f2
  '(* t (bit-shift-right (bit-or (bit-shift-right t 5)
                                 (bit-shift-right t 8))
                         (bit-shift-right t 16))))

(def yv1f3
  '(* t (bit-and (bit-or (bit-shift-right t 9)
                         (bit-shift-right t 13))
                 25
                 (bit-shift-right t 6))))

(def yv1f4
  '(* t (bit-and (bit-shift-right t 11)
                 (bit-shift-right t 8)
                 123
                 (bit-shift-right t 3))))

(def yv1f5
  '(* t (bit-and (* (bit-shift-right t 8)
                    (bit-or (bit-shift-right t 15)
                            (bit-shift-right t 8)))
                 (bit-or 20
                         (bit-shift-right (* (bit-shift-right t 19) 5) t)
                         (bit-shift-right t 3)))))

(def yv1f6
  '(+ (bit-shift-right (* (bit-and (* -1 t) 4095)
                          (bit-and 255
                                   (* t (bit-and t (bit-shift-right t 13)))))
                       12)
      (bit-and 127 (bit-shift-right (* t (bit-and (bit-and 234 (bit-shift-right t 8))
                                                  (bit-shift-right t 3)))
                                    (bit-and 3 (bit-shift-right t 14))))))

(def yv1f7
  '(* t (bit-and (bit-shift-right t (bit-or (bit-shift-right t 9)
                                            (bit-shift-right t 8)))
                 63
                 (bit-shift-right t 4))))

(def forms
  [yv1f1
   yv1f2
   yv1f3
   yv1f4
   yv1f5
   yv1f6
   yv1f7])
