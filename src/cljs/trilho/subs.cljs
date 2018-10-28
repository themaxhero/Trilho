(ns trilho.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::db
 (fn [db]
   db))

(re-frame/reg-sub
 ::title
 (fn [db]
   (:title db)))

(re-frame/reg-sub
 ::editing-title
 (fn [db]
   (:editing-title db)))

(re-frame/reg-sub
 ::title-buffer
 (fn [db]
   (:title-buffer db)))

(re-frame/reg-sub
 ::lists
 (fn [db]
   (:lists db)))

(re-frame/reg-sub
 ::cards
 (fn [db]
   (:cards db)))

(re-frame/reg-sub
 ::tasks
 (fn [db]
   (:tasks db)))

(re-frame/reg-sub
 ::labels
 (fn [db]
   (:labels db)))

(re-frame/reg-sub
 ::comments
 (fn [db]
   (:comments db)))

(re-frame/reg-sub
 ::filters
 (fn [db]
   (:filters db)))

(re-frame/reg-sub
 ::editing-card
 (fn [db]
   (:editing-card-id db)))

(re-frame/reg-sub 
::editing-member-of
(fn [db]
(:editing-member-of db)))