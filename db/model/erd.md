```mermaid
---
title: Piastria schema
config:
  theme: base
  themeVariables:
    background: "#ffffff"
  themeCSS: "svg { background-color: #ffffff; }"
---
erDiagram
  products {
    INTEGER product_id PK
    INTEGER order_id
    TEXT product_name
    TEXT product_unit_name
    INT product_counter_precision
  }
  payment_objects {
    INTEGER payment_object_id PK
    INTEGER order_id
    TEXT payment_object_name
    TEXT payment_object_period
    TEXT payment_object_term
    INT payment_object_pay_delay
  }
  payment_groups {
    INTEGER payment_group_id PK
    INTEGER order_id
    TEXT payment_group_name
    TEXT payment_group_url
    TEXT payment_group_color
  }
  payments {
    INTEGER payment_id PK
    INTEGER order_id
    INTEGER payment_date
    INTEGER payment_period_date
    INTEGER payment_object_id FK
    INTEGER payment_group_id FK
    INTEGER product_id FK
    INTEGER product_counter
    INTEGER payment_amount
    INTEGER commission_amount
  }
  products |o..o{ payments : "product_id"
  payment_objects ||..o{ payments : "payment_object_id"
  payment_groups ||..o{ payments : "payment_group_id"
```
# Tables
| Table | Columns | Primary key | References | Referenced by |
| - | - | - | - | - |
| products | 5 | product_id | - | payments |
| payment_objects | 6 | payment_object_id | - | payments |
| payment_groups | 5 | payment_group_id | - | payments |
| payments | 10 | payment_id | payment_groups, payment_objects, products | - |
