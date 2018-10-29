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
 ::edit-card-name
 (fn [db [_ card-id]]
   (let
    [card (utils/fetch-card db card-id)
     new-card (assoc card :name-editing (not (:name-editing card)))]
     (utils/update-card db card-id new-card))))

(re-frame/reg-event-db
 ::update-card-buffer
 (fn [db [_ card-id new-value]]
   (let
    [card (utils/fetch-card db card-id)
     new-card (assoc card :name-buffer new-value)]
     (utils/update-card db card-id new-card))))

(re-frame/reg-event-db
 ::change-card-name
 (fn [db [_ card-id]]
   (let [card (utils/fetch-card db card-id)]
     (utils/update-card db card-id
                        (-> card
                            (assoc :title (:name-buffer card))
                            (assoc :name-editing false))))))

(re-frame/reg-event-db
 ::change-card-description
 (fn [db [_ card-id]]
   (let [card (utils/fetch-card db card-id)]
     (utils/update-card db card-id
                        (-> card
                            (assoc :description (:description-buffer card))
                            (assoc :description-editing false))))))

(re-frame/reg-event-db
 ::edit-card-description
 (fn [db [_ card-id]]
   (let
    [card (utils/fetch-card db card-id)
     new-card (assoc card :description-editing (not (:description-editing card)))]
     (utils/update-card db card-id new-card))))

(re-frame/reg-event-db
 ::update-card-description
 (fn [db [_ card-id new-value]]
   (let
    [card (utils/fetch-card db card-id)]
     (utils/update-card db card-id (assoc card :description-buffer new-value)))))

(re-frame/reg-event-db
 ::add-task
 (fn [db [_ card-id]]
   (utils/insert-task db card-id (spawner/new-task))))

(re-frame/reg-event-db
 ::remove-task
 (fn [db [_ task-id card-id]]
   (utils/remove-task db task-id card-id)))

(re-frame/reg-event-db
 ::update-task-buffer
 (fn [db [_ task-id new-value]]
   (let
    [task (utils/fetch-task db task-id)
     new-task (assoc task :name-buffer new-value)]
     (utils/update-task db task-id new-task))))

(re-frame/reg-event-db
 ::edit-task
 (fn [db [_ task-id]]
   (let [task (utils/fetch-task db task-id)]
     (utils/update-task db task-id (-> task (assoc :editing true))))))

(re-frame/reg-event-db
 ::change-task-name
 (fn [db [_ task-id]]
   (let [task (utils/fetch-task db task-id)]
     (utils/update-task db task-id (-> task (assoc  :name (:name-buffer task)) (assoc :editing false))))))

(re-frame/reg-event-db
 ::toggle-task
 (fn [db [_ task-id]]
   (let
    [task (utils/fetch-task db task-id)
     new-task (assoc task :checked (not (:checked task)))]
    (utils/update-task db task-id new-task))))

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