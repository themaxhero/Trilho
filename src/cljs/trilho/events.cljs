(ns trilho.events
  (:require
   [re-frame.core :as re-frame]
   [trilho.db :as db]
   [trilho.utils :as utils :refer [debug-log]]
   [trilho.spawner :as spawner]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
::view-state
(fn [db _]
(debug-log db)))

(re-frame/reg-event-db
  ::enter-title-edit
  (fn [db _]
    (assoc db :editing-title (not (:editing-title db)))))

(re-frame/reg-event-db
  ::update-title-buffer
  (fn [db [_ new-name]]
    (assoc db :title-buffer new-name)))

(re-frame/reg-event-db
  ::change-title
  (fn [db _]
    (-> db
        (assoc :title (:title-buffer db))
        (assoc :editing-title false))))

(re-frame/reg-event-db
 ::add-list
 (fn [db _]
   (utils/insert-list db (spawner/new-card-list))))

(re-frame/reg-event-db
 ::remove-list
 (fn [db [_ list-id]]
   (utils/remove-list db list-id)))

(re-frame/reg-event-db
 ::enter-list-title 
 (fn [db [_ list-id]] 
  (let 
  [lists (:lists db)
  list (lists list-id)
  new-list (assoc list :editing-title true)
  new-lists (assoc lists list-id new-list)]
  (assoc db :lists new-lists))))

(re-frame/reg-event-db
 ::update-list-title-buffer 
 (fn [db [_ list-id new-value]]
  (let 
  [lists (:lists db)
  list (lists list-id)
  new-list (assoc list :title-buffer new-value)
  new-lists (assoc lists list-id new-list)]
  (assoc db :lists new-lists))))

(re-frame/reg-event-db
 ::change-list-title
 (fn [db [_ list-id]]
   (let
    [lists (:lists db)
     list (lists list-id)
     title-buffer (:title-buffer list)
     new-list (->  list
                   (assoc :title title-buffer)
                   (assoc :editing-title false))
     new-lists (assoc lists list-id new-list)]
     (assoc db :lists new-lists))))

(re-frame/reg-event-db
 ::add-card
 (fn [db [_ list-id]]
   (utils/insert-card db list-id (spawner/new-card))))

(re-frame/reg-event-db
 ::remove-card
 (fn [db [_ card-id list-id]]
   (utils/remove-card db card-id list-id)))

(re-frame/reg-event-db
 ::add-task
 (fn [db [_ card-id]]
   (utils/insert-task db card-id (spawner/new-task))))

(re-frame/reg-event-db
 ::remove-task
 (fn [db [_ task-id card-id]]
   (utils/remove-task db task-id card-id)))

(re-frame/reg-event-db
 ::toggle-task
 (fn [db [_ task-id]]
   (let
    [tasks (:tasks db)
     task (tasks task-id)
     new-task (update-in task :checked (not (:checked task)))
     new-tasks (assoc tasks task-id new-task)]
    (assoc db  :tasks new-tasks))))

(re-frame/reg-event-db
 ::stop-editing-card
 (fn [db _]
   (-> db
       (assoc :editing-card-id :none)
       (assoc :editing-member-of :none))))

(re-frame/reg-event-db
 ::quit-edit-and-delete
 (fn [db [_ card-id list-id]]
   (-> db
       (assoc :editing-card-id :none)
       (assoc :editing-member-of :none)
       (utils/remove-card card-id list-id))))

(re-frame/reg-event-db
 ::enter-card-editing
 (fn [db [_ card-id list-id]]
   (-> db
     (assoc :editing-member-of list-id)
     (assoc :editing-card-id card-id))))

(re-frame/reg-event-db
 ::close-editing
 (fn [db _]
      (-> db
       (assoc :editing-card-id :none)
       (assoc :editing-member-of :none))))