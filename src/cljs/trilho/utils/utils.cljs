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

(defn get-card-index [db card-id]
  (let
   [cards (:cards db)
    card-index (reduce-kv (fn [acc k v] (if (= (:id v) card-id) k acc)) :none cards)]
    card-index))

(defn get-task-index [db task-id]
  (let
   [tasks (:tasks db)
    task-index (reduce-kv (fn [acc k v] (if (= (:id v) task-id) k acc)) :none tasks)]
    task-index))

(defn fetch-card [db card-id]
  (let
   [cards (:cards db)
    card (reduce-kv (fn [acc _ v] (if (= (:id v) card-id) v acc)) :none cards)]
    card))

(defn fetch-task [db task-id]
  (let
   [tasks (:tasks db)
    task (reduce-kv (fn [acc _ v] (if (= (:id v) task-id) v acc)) :none tasks)]
    task))

(defn update-list [db list-id new-list]
  (let
   [lists (:lists db)]
   (assoc db :lists (assoc lists list-id new-list))))

(defn update-card [db card-id new-card]
  (let
   [cards (:cards db)
    card-index (get-card-index db card-id)]
    (assoc db :cards (assoc cards card-index new-card))))

(defn update-task [db task-id new-task]
  (let
   [tasks (:tasks db)
    task-index (get-task-index db task-id)]
   (assoc db :tasks (assoc tasks task-index new-task))))

(defn insert-list [db list]
  (update db :lists conj list))

(defn insert-card [db list-id card]
  (let
   [list (fetch-list db list-id)
    card-id (:id card)
    new-cards (conj (:cards db) card)
    new-list (update list :card-ids conj card-id)]
    (-> db
         (assoc :cards new-cards)
         (update-in [:lists list-id] #(debug-log new-list)))))

(defn insert-task [db card-id task]
  (let
   [card (fetch-card db card-id)
    task-id (:id task)
    new-card (update card :task-ids conj task-id)]
    (-> db
        (update :tasks conj task)
        (update-card card-id new-card))))

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
    rem-tasks (reduce-kv
               (fn [acc _ card]
                 (into [] (concat acc (tasks-inside db (:id card))))) [] cards)
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
    cards (filterv #(not (= % (nth (:cards db) (get-card-index db card-id)))) (:cards db))]
    (-> db
        (assoc :tasks new-tasks)
        (assoc :cards cards)
        (assoc :lists lists))))

(defn remove-task [db task-id card-id]
  (let
   [card (fetch-card db card-id)
    new-card (assoc card :task-ids (filterv #(not (= % task-id)) (:task-ids card)))
    cards (assoc (:cards db) (get-card-index db card-id) new-card)
    tasks (filterv #(not (= % (nth (:tasks db) (get-task-index db task-id)))) (:tasks db))]
    (-> db
        (assoc :tasks tasks)
        (assoc :cards cards))))

(defn done-tasks [db card-id]
  (let
   [card (fetch-card db card-id)
    card-tasklist (:task-ids card)
    card-tasks (reduce-kv (fn [acc _ v]
                             (into [] (conj acc (fetch-task db v))))
                          [] card-tasklist)]
    (reduce-kv (fn [acc _ v]
                 (if  (:checked (debug-log v)) (+ acc 1) acc))
               0 card-tasks)))

(defn conditional-merge [map condition other]
  (if condition (merge map other) map))