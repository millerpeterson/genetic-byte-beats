(ns genetic-byte-beats.parsing
  (:require [instaparse.core :as insta]))

"CFG for parsing byte beat formulas."
(def parser
  (insta/parser
    "expr          = bitwise-or
     <bitwise-or>  = bitwise-xor | bit-or
     bit-or        = bitwise-or <'|'> bitwise-xor
     <bitwise-xor> = bitwise-and | bit-xor
     bit-xor       = bitwise-xor <'^'> bitwise-and
     <bitwise-and> = bit-shift | bit-and
     bit-and       = bitwise-and <'&'> bit-shift
     <bit-shift>   = add-sub | shift-left | shift-right
     shift-left    = bit-shift <'<<'> add-sub
     shift-right   = bit-shift <'>>'> add-sub
     <add-sub>     = mult-div | add | sub
     add           = add-sub <'+'> mult-div
     sub           = add-sub <'-'> mult-div
     <mult-div>    = term | mult | div | mod
     mult          = mult-div <'*'> term
     div           = mult-div <'/'> term
     mod           = mult-div <'%'> term
     <term>        = number | variable | <'('> bitwise-or <')'>
     variable      = 't'
     <number>      = floating | integer
     floating      = #'-{0,1}\\d+\\.\\d+'
     integer       = #'-{0,1}\\d+'"))

(defn ast-from-parsed
  "Return the AST from the parsed form of a byte beat formula."
  [parsed-form]
  (insta/transform
    {:expr        identity
     :bit-or      (fn [x y] `(~'bit-or ~x ~y))
     :bit-xor     (fn [x y] `(~'bit-xor ~x ~y))
     :bit-and     (fn [x y] `(~'bit-and ~x ~y))
     :shift-left  (fn [x y] `(~'bit-shift-left ~x ~y))
     :shift-right (fn [x y] `(~'bit-shift-right ~x ~y))
     :add         (fn [x y] `(~'+ ~x ~y))
     :sub         (fn [x y] `(~'- ~x ~y))
     :mult        (fn [x y] `(~'* ~x ~y))
     :div         (fn [x y] `(~'/ ~x ~y))
     :mod         (fn [x y] `(~'mod ~x ~y))
     :variable    symbol
     :floating    js/parseFloat
     :integer     js/parseInt}
    parsed-form))

(defn ast-from-string
  "Return the formula AST from a string representation of a byte
  beat formula."
  [form-str]
  (ast-from-parsed
    (parser
       (clojure.string/replace form-str " " ""))))
