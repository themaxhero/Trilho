(ns trilho.utils
  (:require [re-frame.core :as re-frame]
  [trilho.subs :as subs]))

(defn debug-log [item]
  (println item)
  item)

(defn fetch-list [db list-id]
  (let
   [lists (:lists db)
    list (nth lists (debug-log list-id))]
    list))

(defn fetch-card [db card-id]
  (let
   [cards (:cards db)
    card (nth cards card-id)]
    card))

(defn fetch-task [db task-id]
  (let
   [tasks (:tasks db)
    task (nth tasks task-id)]
    task))

(defn update-list [db list-id new-list]
  (let
   [lists (:lists db)]
   (assoc db :lists (assoc lists list-id new-list))))

(defn update-card [db card-id new-card]
  (let
   [cards (:cards db)]
   (assoc db :cards (assoc cards card-id new-card))))

(defn update-task [db task-id new-task]
  (let
   [tasks (:tasks db)]
   (assoc db :tasks (assoc tasks task-id new-task))))

(defn insert-list [db list]
  (update db :lists conj list))

(defn insert-card [db list-id card]
  (let
   [list (fetch-list db list-id)
    card-id (count (:cards db))
    new-cards (conj (:cards db) card)
    new-list (update list :card-ids conj card-id)]
    (-> db
         (assoc :cards new-cards)
         (update-in [:lists list-id] #(debug-log new-list)))))

(defn insert-task [db card-id task]
  (let
   [card (fetch-card db card-id)
    task-id (count (:tasks db))
    new-card (update card :task-ids conj task-id)]
    (-> db
        (update :tasks conj task)
        (assoc :cards card-id new-card))))

(defn cards-inside [db list-id]
  (let
   [list (fetch-list db list-id)]
    (:card-ids list)))

(defn tasks-inside [db card-id]
  (let
   [card (fetch-card db card-id)]
    (:task-ids card)))

(defn remove-tasks-from [db list-id]
  (let
   [cards (:cards db)
    tasks (:tasks db)
    rem-tasks (reduce-kv (fn [acc card-id _] (into [] (concat acc (tasks-inside db card-id)))) [] cards)
    new-tasks (remove #(contains? rem-tasks %) (:tasks db))]
    (assoc db :tasks new-tasks)))

(defn remove-cards-from [db list-id]
  (let
   [list (fetch-list db list-id)
    cards (:cards db)
    rem-cards (:card-ids list)
    new-cards (remove #(contains? rem-cards %) cards)]
    (assoc db :cards new-cards)))

(defn remove-list [db list-id]
  (let
   [lists (filterv #(not (= ((:lists db) list-id) %)) (:lists db))]
    (-> db
        (remove-tasks-from list-id)
        (remove-cards-from list-id)
        (assoc :lists lists))))

(defn remove-card [db card-id list-id]
  (let
   [list (fetch-list db list-id)
    new-list (assoc list :card-ids (filterv #(not (= % card-id)) (:card-ids list)))
    lists (assoc (:lists db) list-id new-list)
    rem-tasks (tasks-inside db card-id)
    new-tasks (remove #(contains? rem-tasks %) (:tasks db))
    cards (filterv #(not (= (nth (:cards db) card-id) %)) (:cards db))]
    (-> db
        (assoc :tasks new-tasks)
        (assoc :cards cards)
        (assoc :lists lists))))

(defn remove-task [db task-id card-id]
  (let
   [card (fetch-card db card-id)
    new-card (update card :task-ids filterv #(not (= % task-id)) (:task-ids card))
    cards (assoc (:cards db) card-id new-card)
    tasks (filterv #(not (= % ((:tasks db) task-id))) (:tasks db))]
    (-> db
        (assoc :tasks tasks)
        (assoc :cards cards))))


(defn get-done-tasks [card-id]
  (let
   [cards (re-frame/subscribe [::subs/cards])
    card (@cards card-id)
    tasks (re-frame/subscribe [::subs/tasks])
    card-tasklist (:task-ids card)
    card-tasks (reduce-kv (fn [acc k v] (conj acc (@tasks v))) [] card-tasklist)]
    (reduce-kv (fn [acc k v] (if (:checked v) (+ acc 1) acc )) 0 card-tasks)))
