(ns trilho.task
   (:require 
   [re-frame.core :as re-frame]
   [trilho.subs :as subs]
   [trilho.events :as events]))

(defn edit-button [task-id]
  ([:div.edit-task
    {:class "fas fa-pencil-alt"
     :on-click #(re-frame/dispatch [::events/edit-task task-id])}]))

(defn delete-button [task-id]
  ([:div.delete-task
    {:class "far fa-trash-alt"
     :on-click #(re-frame/dispatch [::events/remove-task task-id])}]))

(defn render [task-id]
  (let
   [tasks #(re-frame/subscribe [::subs/tasks])
    task (@tasks task-id)
    name (:name task)
    marked (:checked task)]
    [:div.task {:key (:id task)}
     [:input
      {:type "checkbox"
       :checked marked
       :on-click #(re-frame/dispatch [::events/toggle-task task-id])}]
     name
     (edit-button task-id)
     (delete-button task-id)]))