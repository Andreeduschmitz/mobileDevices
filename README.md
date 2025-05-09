# CLI Socket Chat

Este projeto é uma implementação simples de um chat via **Sockets em Java**, com suporte ao envio de **mensagens** e **arquivos** entre os usuários.

## Alunos:
- André Eduardo Schmitz
- Ana Beatriz Martins da Silva
- Eduarda Stipp Rech
- Mariana Rossdeutscher Waltrick Lima

## Como executar

1. Execute a classe `Main.java`.
2. Escolha o modo de operação: **Cliente** ou **Servidor**.
3. Informe a **porta** para comunicação.  
   - Se estiver executando como **Cliente**, também será necessário informar o **IP do servidor**.
4. Use os seguintes comandos disponíveis no terminal:
   - `/message <destinatário> <mensagem>` – Envia uma mensagem para o destinatário.
   - `/file <destinatário> <caminho do arquivo>` – Envia um arquivo para o destinatário.
   - `/users` – Lista todos os usuários conectados.
   - `/sair` – Encerra a conexão do cliente com o servidor.
5. No diretório do projeto há o arquivo **logs.txt** que salva o IP, data e hora que os usuários conectaram e desconectaram do servidor.