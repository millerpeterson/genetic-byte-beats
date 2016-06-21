(ns genetic-byte-beats.web-ui
  (:require [genetic-byte-beats.forms.erlehmann :as erlehmann]
            [genetic-byte-beats.forms.evolved :as evolved]
            [genetic-byte-beats.parsing :as parsing]
            [reagent.core :as r]))



(defn play-controls
  [history commands]
  [:div
   [:button
    {:on-click #((:play-and-print commands) (last @history))
     :class "btn btn-primary btn-large"}
    "Play"]
   [:button
    {:on-click (:stop commands)
     :class "btn btn-primary btn-large"}
    "Stop"]])

(defn genetic-controls
  [_ commands]
  [:div
   [:button
    {:on-click #((:breed commands) (into erlehmann/forms evolved/forms))
     :class "btn btn-primary btn-large"}
    "Crossover"]
   [:button
    {:on-click #((:mutate commands) :perturb)
     :class "btn btn-primary btn-large"}
    "Mutate"]
   [:button
    {:on-click #((:mutate commands) :complexify)
     :class "btn btn-primary btn-large"}
    "Complexify"]
   [:button
    {:on-click #((:mutate commands) :simplify)
     :class "btn btn-primary btn-large"}
    "Simplify"]])

(defn history-controls
  [history commands]
  [:div
   [:button
    {:on-click #((:undo commands))
     :class "btn btn-primary btn-large"}
    "Undo"]
   [:button
    {:on-click #(do
                 ((:new-line commands) (into erlehmann/forms evolved/forms))
                 ((:play-and-print commands) (first @history)))
     :class "btn btn-primary btn-large"}
    "New Line"]])

(defn cell-history
  [history]
  [:ul {:id "cell-history"}
   (for [cell-ast (reverse @history)]
     (let [form-string (parsing/string-from-ast cell-ast)]
       ^{:key form-string} [:li form-string]))])

(defn app
  [history commands]
  [:div
   [play-controls history commands]
   [genetic-controls history commands]
   [history-controls history commands]
   [cell-history history]])

(defn ^:export run [history commands]
  (r/render [app history commands]
            (js/document.getElementById "app")))