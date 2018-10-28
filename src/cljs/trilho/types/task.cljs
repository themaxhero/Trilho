(ns trilho.task
   (:require 
   [re-frame.core :as re-frame]
   [trilho.subs :as subs]
   [trilho.events :as events]
   [trilho.utils :as utils :refer[conditional-merge debug-log]]))

(defn edit-button [task-id editing card-id]
  [:div.edit-task
   (if editing
     {:class "fas fa-check"
      :style {:margin-top "5px"}
      :on-click #(re-frame/dispatch [::events/change-task-name task-id])}
     {:class "fas fa-pencil-alt"
      :on-click #(re-frame/dispatch [::events/edit-task task-id])})])

(defn delete-button [task-id editing card-id]
  [:div.delete-task
   {:class "far fa-trash-alt"
    :style (if editing {:margin-top "5px"} {})
    :on-click #(re-frame/dispatch [::events/remove-task task-id card-id])}])

(defn task-attr [task-id marked]
  {:type "checkbox"
   :checked (if marked {} "")
   :on-change #(re-frame/dispatch [::events/toggle-task task-id])})

(defn render [task-id card-id]
  (let
   [db (re-frame/subscribe [::subs/db])
    task (utils/fetch-task @db task-id)
    name (:name task)
    name-buffer (:name-buffer task)
    marked (:checked task)
    editing (:editing task)
    attr (task-attr task-id marked)]
    [:div.task {:key (:id task)}
     [:input.task-check attr]
     [:div.task-name
      (if editing
        [:input
         {:value name-buffer
          :on-change #(re-frame/dispatch [::events/update-task-buffer task-id (-> % .-target .-value)])}]
        [:div name])
      (edit-button task-id editing card-id)
      (delete-button task-id editing card-id)]]))