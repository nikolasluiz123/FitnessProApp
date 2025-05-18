# 📱 Fitness Pro Solutions

Este é um aplicativo Android nativo desenvolvido com o objetivo de auxiliar usuários no acompanhamento 
de treinos, dietas e compromissos com profissionais da área da saúde e fitness. O projeto está organizado 
em módulos independentes, facilitando a manutenção e evolução contínua da aplicação.

## 🛠️ Tecnologias Utilizadas

Este projeto utiliza as seguintes bibliotecas e tecnologias modernas do ecossistema Android:

- **[Kotlin](https://kotlinlang.org/)** como linguagem principal
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** para construção de interfaces declarativas
- **[Hilt](https://developer.android.com/training/dependency-injection/hilt-android)** para injeção de dependência
- **[Room](https://developer.android.com/jetpack/androidx/releases/room)** para persistência local de dados
- **[Retrofit](https://square.github.io/retrofit/)** para chamadas HTTP ao serviço REST
- **[Material Design 3](https://m3.material.io/)** para interface moderna e consistente
- **[Firebase](https://firebase.google.com/)** para diversas funcionalidades:
    - [Authentication](https://firebase.google.com/docs/auth) (login via e-mail/senha e Google)
    - [Cloud Firestore](https://firebase.google.com/docs/firestore) (dados do chat)
    - [Crashlytics](https://firebase.google.com/docs/crashlytics) (monitoramento de falhas)
    - [Analytics](https://firebase.google.com/docs/analytics) (eventos de uso do app)
    - [Performance Monitoring](https://firebase.google.com/docs/perf-mon) (análise de desempenho)
    - [Cloud Messaging](https://firebase.google.com/docs/cloud-messaging) (notificações)
- **[WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)** para sincronização periódica entre o dispositivo e o serviço REST

---

## 🌐 Funcionamento Geral

O aplicativo permite uso parcial offline, armazenando os dados localmente com o Room. A sincronização 
com o servidor é feita em segundo plano utilizando o **WorkManager**, o que permite que os dados sejam
enviados ou recebidos automaticamente, mesmo sem interação do usuário. O intervalo atual entre 
sincronizações é de aproximadamente 30 segundos.

A arquitetura implementada simula um ambiente de produção real, incluindo:

- Organização modular do código
- Utilização de **tags e releases** no repositório
- Pipeline automatizado que realiza build e distribuição de versões a cada push no branch `master`

---

## 🔐 Módulo Geral (Autenticação)

Para proteger e segmentar os dados de cada usuário, o aplicativo utiliza o **Firebase Authentication**, 
permitindo login via e-mail/senha e autenticação com Google. Essa autenticação também é integrada com 
o Firestore, garantindo regras de segurança adequadas.

O fluxo de autenticação e cadastro permite que os usuários escolham perfis específicos:

- **Membro**
- **Instrutor**
- **Nutricionista**

Durante o cadastro, o usuário pode informar horários de treino ou trabalho, o que influencia a 
disponibilidade para agendamentos futuros.

- 🔗 [Fluxo de autenticação no Figma](https://www.figma.com/design/wLlNhCNKgwoVWFDG31QM5s/Fitness-Pro-Solutions?node-id=2144-25267&t=AydUOqR25ap3grE6-1)
- 🔗 [Fluxo de cadastro no Figma](https://www.figma.com/design/wLlNhCNKgwoVWFDG31QM5s/Fitness-Pro-Solutions?node-id=2145-1989&t=AydUOqR25ap3grE6-1)

---

## 📆 Módulo de Agenda

Esse módulo foi criado para organizar os compromissos de treino e avaliações físicas, com funcionalidades 
distintas para membros e profissionais.

### Funcionalidades principais

- **Sugestão de compromissos** por parte do Membro para os profissionais
- **Agendamentos manuais** criados pelos profissionais para os membros
- **Agendamento recorrente** (exclusivo para Instrutores), ideal para treinos repetitivos semanais

### Notificações implementadas

1. Novo compromisso criado ou sugerido
2. Alterações no horário do compromisso
3. Mudança de status (ex: confirmado, cancelado)

> Agendamentos recorrentes também geram automaticamente registros de treino, que serão utilizados no módulo de Treinamento.

- 🔗 [Fluxo de telas do módulo de agenda](https://www.figma.com/design/wLlNhCNKgwoVWFDG31QM5s/Fitness-Pro-Solutions?node-id=2024-17784&t=AydUOqR25ap3grE6-1)

---

## 💬 Módulo de Chat

Este módulo utiliza **Cloud Firestore** como base de dados em tempo real para troca de mensagens 
entre os usuários. Ele é protegido com regras baseadas na autenticação do Firebase, garantindo
privacidade nas conversas.

As mensagens são sincronizadas automaticamente entre os dispositivos dos participantes da conversa, 
com suporte a notificações via **Firebase Cloud Messaging**.

---

## 🔁 Sincronização com WorkManager

Toda a comunicação com o serviço REST (implementado por mim) é feita de forma assíncrona usando 
o **WorkManager**, o que permite:

- Atualização dos dados sem depender de interação do usuário
- Operações agendadas mesmo com o app fechado
- Consumo eficiente de bateria

---

## 🚀 Considerações Finais

Este projeto continua em evolução e serve como laboratório para explorar boas práticas de 
desenvolvimento Android moderno. Caso queira contribuir ou relatar problemas, sinta-se à 
vontade para abrir uma *issue* ou um *pull request*.

---