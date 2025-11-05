# DesenvolvimentoSoftwareCorp

## Padaria PãoKetin

```mermaid
erDiagram
    PADARIA ||--o{ FUNCIONARIO : Emprega
    PADARIA ||--o{ FORNADA : Contem

    FUNCIONARIO ||--o{ TAREFA : Executa

    FORNADA }o--o{ PAO : Produz
    FUNCIONARIO }o--o{ FORNADA : Realiza

```
