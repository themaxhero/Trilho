(ns trilho.card-list
  (:require
   [clojure.string :as string]
   [re-frame.core :as re-frame]
   [trilho.subs :as subs]
   [trilho.events :as events]
   [trilho.card :as card]))

(defn ghost-card [list-id]
  (let
   [lists (re-frame/subscribe [::subs/lists])
    list (@lists list-id)
    cards (:card-ids list)
    text (if (> (count cards) 0) "Add another card" "Add a new card")]
    [:div.card.ghost-card
     {:on-click #(re-frame/dispatch [::events/add-card list-id])}
     [:b.noselect text]]))

(defn list-title [list-id]
  (let
   [lists (re-frame/subscribe [::subs/lists])
    list (@lists list-id)
    title (:title list)]
    [:b.noselect {:on-click #(re-frame/dispatch [::events/enter-list-title list-id])} title]))

(defn title-editing-input [list-id]
  (let
   [lists (re-frame/subscribe [::subs/lists])
    list (@lists list-id)
    title-buffer (:title-buffer list)]
    [:div.list-title-editing
     [:input
      {:value title-buffer
       :on-change #(re-frame/dispatch [::events/update-list-title-buffer list-id (-> % .-target .-value)])}]
     [:i.side-button
      {:class "fas fa-check"
       :on-click #(re-frame/dispatch [::events/change-list-title list-id])}]]))
  
(defn render-title [list-id]
  (let
   [lists (re-frame/subscribe [::subs/lists])
    list (@lists list-id)
    title (:title list)
    title-buffer (:title-buffer list)
    editing (:editing-title list)]
    (if editing
      (title-editing-input list-id)
      (list-title list-id))))

(defn settings-icon [list-id]
  (let
   [lists (re-frame/subscribe [::subs/lists])
    list (@lists list-id)
    editing (:editing-title list)]
    [:i.list-settings.noselect
     {:class "fas fa-cog"
      :style (if editing {:margin-top "4px"} {})
      :title (string/join ": " ["Settings for list of id" list-id])
      :on-click #()}])) ;  TODO: Add an options dropdown

(defn render [list-id]
  (let
   [lists (re-frame/subscribe [::subs/lists])
    list (@lists list-id)
    title (:title list)
    card-ids (:card-ids list)]
    [:div.list {:key (:id list)}
     [:div.list-title (render-title list-id) (settings-icon list-id)]
     (reduce-kv (fn [acc _ card-id](conj acc (card/render card-id list-id))) '() card-ids)
     (ghost-card list-id)]))