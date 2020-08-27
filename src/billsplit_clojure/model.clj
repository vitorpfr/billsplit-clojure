(ns billsplit-clojure.model
  (:require [schema.core :as s]))

(s/defschema PosInt (s/pred pos-int?))

(s/defschema Product
  {:name     s/Str
   :quantity PosInt
   :price    java.math.BigDecimal
   :fraction (s/maybe (s/either clojure.lang.Ratio s/Int))})

s/conditional

(s/defschema Person
  {:name s/Str
   :products [Product]
   (s/optional-key :to-pay) (s/maybe java.math.BigDecimal)})

(s/defschema Bill
  {s/Str Person})
