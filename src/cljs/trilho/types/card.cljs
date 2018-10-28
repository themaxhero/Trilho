(ns trilho.card
  (:require
   [re-frame.core :as re-frame]
   [clojure.string :as string]
   [trilho.subs :as subs]
   [trilho.events :as events]
   [trilho.task :as task]
   [trilho.utils :as utils]))

(defn ghost-task []
  ())

(defn comment-box [card-id]
  [:div.comment-box
   [:textarea.comment-text]
   [:div.comment-box-bottom [:button.comment-button]]])

(defn edit-button [card-id list-id]
  [:div.card-button
   {:class "fas fa-pencil-alt"
    :on-click #(re-frame/dispatch [::events/enter-card-editing card-id list-id])}])

(defn remove-button [card-id list-id]
  [:div.card-button
   {:class "fas fa-trash"
    :on-click #(re-frame/dispatch [::events/remove-card card-id list-id])}])

(defn render [card-id list-id]
  (let
   [db (re-frame/subscribe [::subs/db])
    card (utils/fetch-card @db card-id)
    title (:title card)
    task-ids (:task-ids card)]
    [:div.card.noselect
     {:key (:id card)}
     [:b.noselect title]
     [:div
      (edit-button card-id list-id)
      (remove-button card-id list-id)]]))

(defn render-comments [card-id]
[:div.card-comments (comment-box card-id)])

(defn render-description [card-id]
  [:div.card-description
   [:div {:class "fas fa-bars" :style {:margin "2px 4px"}}]
   [:div {:style {:width "100%"}}
    [:b {:style {:margin-left "8px"}} "Descrição"]
    [:textarea.comment-text {:style {:width "90%" :height "90%" :margin "8px"}}]]])

(defn progress-bar [card-id tasks]
  (let
   [db (re-frame/subscribe [::subs/db])
    done-tasks (utils/done-tasks @db card-id)
    all-tasks (count tasks)
    ratio (if (<= all-tasks 0) 0 (/ (* done-tasks 100) all-tasks))]
    [:div.progress-bar
     [:div.progress-bar
      {:style
       {:width (str ratio "%") :background-color "#0F0"}}]]))

(defn check-list [tasks card-id]
  (reduce-kv (fn [acc _ v] (conj acc (task/render v card-id))) '() tasks))

(defn render-checklist [card-id]
  (let
   [db (re-frame/subscribe [::subs/db])
    card (utils/fetch-card @db card-id)
    tasks (:task-ids card)
    done-tasks (utils/done-tasks @db card-id)]
    (if (< (count tasks) 0) ()
        [:div.checklist-container
         [:div.checklist-title
          [:div.checklist-top-left
           [:i {:class "fas fa-clipboard-list" :style {:margin "4px"}}]
           (if (<= (count tasks) 0) "0%"
               (str (string/join (take 4 (str (/ (* done-tasks 100) (count tasks))))) "%"))]
          [:div.checklist-top-right
           (str "Checklist (" done-tasks "/" (count tasks) ")")
           (progress-bar card-id tasks)]]
         (check-list tasks card-id)])))

(defn render-editing-left [card-id]
  (let
   [db (re-frame/subscribe [::subs/db])
    card (utils/fetch-card @db card-id)
    tasks (:task-ids card)]
    [:div.left-side
     (render-description card-id)
     (if (<= (count tasks) 0)
       ()
       (render-checklist card-id))
     (render-comments card-id)]))

(defn render-add-to [card-id]
  [:div.add-to-card "Adicionar ao cartão"
   [:div.card.noselect
    {:on-click #(re-frame/dispatch [::events/add-task card-id])}
    "Adicionar uma task"]])

(defn render-actions [card-id list-id]
  [:div.actions "Ações"
   [:div.card.noselect {:on-click #(re-frame/dispatch [::events/quit-edit-and-delete card-id list-id])}
    "Excluir cartão"]])

(defn render-editing-right [card-id list-id]
  [:div.right-side
   (render-add-to card-id)
   (render-actions card-id list-id)])

(defn render-container [card-id list-id]
   [:div.editing-container
    (render-editing-left card-id)
    (render-editing-right card-id list-id)])

(defn render-title [card-id]
  (let
   [db (re-frame/subscribe [::subs/db])
    card (utils/fetch-card @db card-id)]
    [:div.card-title.noselect
     (if-not (:name-editing card)
       [:div {:on-click #(re-frame/dispatch [::events/edit-card-name card-id])} (:title card)]
       [:div
        [:input
         {:value (:name-buffer card)
          :on-change #(re-frame/dispatch [::events/update-card-buffer card-id (-> % .-target .-value)])}]
        [:div {:class "fas fa-check"
               :style {:margin-left "8px" :margin-top "5px" :cursor "pointer"}
               :on-click #(re-frame/dispatch [::events/change-card-name card-id])}]])]))

(defn render-editing []
  (let
   [card-id (re-frame/subscribe [::subs/editing-card])
    list-id (re-frame/subscribe [::subs/editing-member-of])]
    [:div.editing-card
     (render-title @card-id)
     (render-container @card-id @list-id)]))