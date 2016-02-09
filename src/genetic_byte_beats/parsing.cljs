(ns genetic-byte-beats.parsing
  (:require [instaparse.core :as insta]))

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