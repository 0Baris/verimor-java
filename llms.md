# Verimor Java SDK — reference for AI assistants

Unofficial Java (11+) client for Verimor SMS, Switch and WhatsApp. Maven coordinates:
`io.github.0baris:verimor`. The jar is published on the GitHub Releases page; install it
into your local Maven repository (see docs/en/installation.md).

## Setting the server

```java
SmsClient sms = new SmsClient(new SmsClientOptions(username, password)
        .defaultSender("VERIMOR")
        .baseUri(URI.create("https://sms.example.test")));
```

`SwitchClientOptions(apiKey)` and `WhatsAppClientOptions(apiKey)` take `.baseUri(...)` the
same way; leave it out to use Verimor's server.

## Calling operations

Each client groups operations into services: `client.{service}().{operation}(...)`. Optional
parameters have an overload without them. Request models live in
`com.bariscemant.verimor.{sms|switchapi|whatsapp}.generated.model` and use fluent setters;
pass `""` for `username`/`password` in SMS request models, the client fills them in.

## Errors

A non-2xx answer throws `VerimorApiException` with the status code and body;
`UnexpectedResponseException` means a 2xx body did not match the documented shape.

## Products and authentication

| Product | Credentials | Default server | Environment variables used by the examples |
| --- | --- | --- | --- |
| SMS | username + password, optional default sender (`source_addr`) | `https://sms.verimor.com.tr` | `VERIMOR_SMS_USERNAME`, `VERIMOR_SMS_PASSWORD`, `VERIMOR_SMS_SENDER` |
| Switch | API key (`key`) | `https://api.bulutsantralim.com` | `VERIMOR_SWITCH_API_KEY` |
| WhatsApp | API key (`x-api-key` header) | `https://wapi.verimor.com.tr` | `VERIMOR_WHATSAPP_API_KEY` |

Every client talks to Verimor's server by default. Pass a different base URL to use a proxy,
a test server or a mock; every example reads it from `VERIMOR_BASE_URL`. The client adds the
credentials to each request itself (query, body or header, as the operation requires), so
request values never carry them. Keep credentials on the server side.

The SMS default sender is sent as `source_addr` wherever an operation accepts one and the
call does not set it.

## Running an example

Every operation has a runnable example. Set the environment variables above and run the
file; set `VERIMOR_BASE_URL` to point it at your own server. The repository's
`scripts/run_examples.py` runs all of them against a local recording server, which never
contacts Verimor.

## Operations

Each operation: HTTP method and path, the call, and the runnable example file. Values are samples from the API documentation.

### SMS

#### addBlacklistEntry — `POST /v2/blacklists`

Kara Liste Ekleme

Java — [`examples/src/main/java/examples/operations/sms/AddBlacklistEntryExample.java`](examples/src/main/java/examples/operations/sms/AddBlacklistEntryExample.java)

```java
client.blacklist().addBlacklistEntry("905001112233")
```


#### balance — `GET /v2/balance`

Bakiye Sorgulama

Java — [`examples/src/main/java/examples/operations/sms/BalanceExample.java`](examples/src/main/java/examples/operations/sms/BalanceExample.java)

```java
client.balances().balance()
```


#### cancel — `POST /v2/cancel/{id}`

Gönderim İptali

Java — [`examples/src/main/java/examples/operations/sms/CancelExample.java`](examples/src/main/java/examples/operations/sms/CancelExample.java)

```java
client.campaigns().cancel(
        "123",
        new PostV2CancelIdRequest()
            .username("")
            .password(""))
```


#### deleteBlacklistEntry — `DELETE /v2/blacklists/{id}`

Kara Listeden Silme

Java — [`examples/src/main/java/examples/operations/sms/DeleteBlacklistEntryExample.java`](examples/src/main/java/examples/operations/sms/DeleteBlacklistEntryExample.java)

```java
client.blacklist().deleteBlacklistEntry("123")
```


#### listBlacklistEntries — `GET /v2/blacklists`

Kara Liste Görüntüleme

Java — [`examples/src/main/java/examples/operations/sms/ListBlacklistEntriesExample.java`](examples/src/main/java/examples/operations/sms/ListBlacklistEntriesExample.java)

```java
client.blacklist().listBlacklistEntries()
```


#### listInboundMessages — `GET /v2/inbound_messages`

Gelen SMS Sorgulama

Java — [`examples/src/main/java/examples/operations/sms/ListInboundMessagesExample.java`](examples/src/main/java/examples/operations/sms/ListInboundMessagesExample.java)

```java
client.reports().listInboundMessages()
```


#### listIysCampaignConsents — `GET /v2/iys/campaigns/{id}/consents`

İYS İzinleri Sorgulama

Java — [`examples/src/main/java/examples/operations/sms/ListIysCampaignConsentsExample.java`](examples/src/main/java/examples/operations/sms/ListIysCampaignConsentsExample.java)

```java
client.iys().listIysCampaignConsents(1L)
```


#### listIysCampaigns — `GET /v2/iys/campaigns`

İYS Kampanyaları Listeleme

Java — [`examples/src/main/java/examples/operations/sms/ListIysCampaignsExample.java`](examples/src/main/java/examples/operations/sms/ListIysCampaignsExample.java)

```java
client.iys().listIysCampaigns()
```


#### listSenderIds — `GET /v2/headers`

Başlık Yönetimi

Java — [`examples/src/main/java/examples/operations/sms/ListSenderIdsExample.java`](examples/src/main/java/examples/operations/sms/ListSenderIdsExample.java)

```java
client.senderIds().listSenderIds()
```


#### send — `POST /v2/send.json`

SMS Gönderme (JSON)

Java — [`examples/src/main/java/examples/operations/sms/SendExample.java`](examples/src/main/java/examples/operations/sms/SendExample.java)

```java
client.campaigns().send(
        new SendSmsJsonRequest()
            .username("")
            .password("")
            .messages(List.of(
                new SendSmsJsonRequestMessagesInner()
                    .dest("905111111111,905111111112")
                    .msg("Deneme Mesaj"))))
```


#### sendLegacy — `GET /v2/send`

SMS Gönderme (GET)

Java — [`examples/src/main/java/examples/operations/sms/SendLegacyExample.java`](examples/src/main/java/examples/operations/sms/SendLegacyExample.java)

```java
client.campaigns().sendLegacy("905001112233", "Merhaba")
```


#### sendOtp — `POST /v2/otp`

OTP Gönderme

Java — [`examples/src/main/java/examples/operations/sms/SendOtpExample.java`](examples/src/main/java/examples/operations/sms/SendOtpExample.java)

```java
client.campaigns().sendOtp(
        new OtpRequest()
            .username("")
            .password("")
            .dest("905001234567")
            .code("482931"))
```


#### status — `GET /v2/status`

Rapor Sorgulama (API ID)

Java — [`examples/src/main/java/examples/operations/sms/StatusExample.java`](examples/src/main/java/examples/operations/sms/StatusExample.java)

```java
client.reports().status(
        1L,
        null /* dest */,
        null /* greaterThan */,
        null /* customId */)
```


#### submitIysConsents — `POST /v2/iys_consents.json`

İzin Yönetimi

Java — [`examples/src/main/java/examples/operations/sms/SubmitIysConsentsExample.java`](examples/src/main/java/examples/operations/sms/SubmitIysConsentsExample.java)

```java
client.iys().submitIysConsents(
        new PostV2IysConsentsJsonRequest()
            .username("")
            .password("")
            .sourceAddr(env("VERIMOR_SMS_SENDER", "VERIMOR"))
            .consents(List.of(
                new PostV2IysConsentsJsonRequestConsentsInner()
                    .type("MESAJ")
                    .source("HS_WEB")
                    .status("ONAY")
                    .recipientType("BIREYSEL")
                    .consentDate(OffsetDateTime.parse("2022-04-14T13:30:30+03:00"))
                    .recipient("905001112233"))))
```


### Switch

#### answer — `POST /answer`

Çağrıyı Cevaplama (POST)

Java — [`examples/src/main/java/examples/operations/switchapi/AnswerExample.java`](examples/src/main/java/examples/operations/switchapi/AnswerExample.java)

```java
client.calls().answer(
        new AnswerCallPostRequest()
            .id("736eaf7e-4cc4-44ab-8dbe-16b18e9618b1"))
```


#### answerLegacy — `GET /answer/{id}`

Çağrıyı Cevaplama (GET)

Java — [`examples/src/main/java/examples/operations/switchapi/AnswerLegacyExample.java`](examples/src/main/java/examples/operations/switchapi/AnswerLegacyExample.java)

```java
client.calls().answerLegacy("736eaf7e-4cc4-44ab-8dbe-16b18e9618b1")
```


#### bridge — `GET /bridge`

Çağrı Bağlama

Java — [`examples/src/main/java/examples/operations/switchapi/BridgeExample.java`](examples/src/main/java/examples/operations/switchapi/BridgeExample.java)

```java
client.calls().bridge("905111111111", "905111111112")
```


#### createAnnouncement — `POST /announcements`

Yeni Ses Dosyası Yükleme

Java — [`examples/src/main/java/examples/operations/switchapi/CreateAnnouncementExample.java`](examples/src/main/java/examples/operations/switchapi/CreateAnnouncementExample.java)

```java
client.announcements().createAnnouncement("dosya adı", "base64")
```


#### createBlockedNumber — `POST /blocked_numbers`

Kara Listeye Ekleme

Java — [`examples/src/main/java/examples/operations/switchapi/CreateBlockedNumberExample.java`](examples/src/main/java/examples/operations/switchapi/CreateBlockedNumberExample.java)

```java
client.blacklist().createBlockedNumber("05111111111")
```


#### createContact — `POST /contacts`

Kişi Ekleme

Java — [`examples/src/main/java/examples/operations/switchapi/CreateContactExample.java`](examples/src/main/java/examples/operations/switchapi/CreateContactExample.java)

```java
client.contacts().createContact("Verimor", "Telekomünikasyon", "05111111111")
```


#### createContactGroup — `POST /contact_groups`

Grup Oluşturma

Java — [`examples/src/main/java/examples/operations/switchapi/CreateContactGroupExample.java`](examples/src/main/java/examples/operations/switchapi/CreateContactGroupExample.java)

```java
client.contacts().createContactGroup("Müşteriler")
```


#### createFaxDocumentUrl — `POST /fax_document_url`

Faks Belgesi URL'si İsteme

Java — [`examples/src/main/java/examples/operations/switchapi/CreateFaxDocumentUrlExample.java`](examples/src/main/java/examples/operations/switchapi/CreateFaxDocumentUrlExample.java)

```java
client.fax().createFaxDocumentUrl("e28e5d48-05d8-11e8-663a-fde60c59425c")
```


#### createFaxOrder — `POST /fax_orders`

Faks Gönderimi

Java — [`examples/src/main/java/examples/operations/switchapi/CreateFaxOrderExample.java`](examples/src/main/java/examples/operations/switchapi/CreateFaxOrderExample.java)

```java
client.fax().createFaxOrder("901234567891", "JVBERi0xLjQK")
```


#### createIvrCampaign — `POST /ivr_campaigns.json`

Otomatik Arama Kampanyası Oluşturma

Java — [`examples/src/main/java/examples/operations/switchapi/CreateIvrCampaignExample.java`](examples/src/main/java/examples/operations/switchapi/CreateIvrCampaignExample.java)

```java
client.ivrCampaigns().createIvrCampaign(
        new CreateIvrCampaignRequest()
            .callType("ivr")
            .name("Memnuniyet anketi")
            .phoneList(List.of(
                new CreateIvrCampaignRequestPhoneListInner()
                    .phone("05111111111")
                    .phrase("#429 12/05/2017 #430 102.45 #431")
                    .lang("tr-TR"),
                new CreateIvrCampaignRequestPhoneListInner()
                    .phone("05111111112")
                    .phrase("#429 12/05/2017 #430 65.12 #431")
                    .lang("tr-TR"))))
```


#### createRecordingUrl — `POST /recording_url`

Ses Kaydı için Geçici URL Oluşturma

Java — [`examples/src/main/java/examples/operations/switchapi/CreateRecordingUrlExample.java`](examples/src/main/java/examples/operations/switchapi/CreateRecordingUrlExample.java)

```java
client.records().createRecordingUrl("3f2504e0-4f89-41d3-9a0c-0305e82c3301")
```


#### createVoicemailRecordingUrl — `POST /voicemail_recording_url`

Telesekreter Ses Kaydı için Geçici URL Oluşturma

Java — [`examples/src/main/java/examples/operations/switchapi/CreateVoicemailRecordingUrlExample.java`](examples/src/main/java/examples/operations/switchapi/CreateVoicemailRecordingUrlExample.java)

```java
client.records().createVoicemailRecordingUrl(
        "12345678-1234-5678-4321-123456789012")
```


#### createWebphoneToken — `POST /webphone_tokens`

Dahili için Token Alma (IFrame ile kullanmak için)

Java — [`examples/src/main/java/examples/operations/switchapi/CreateWebphoneTokenExample.java`](examples/src/main/java/examples/operations/switchapi/CreateWebphoneTokenExample.java)

```java
client.users().createWebphoneToken("1001")
```


#### deleteAnnouncement — `DELETE /announcements/{id}`

Ses Dosyası Silme

Java — [`examples/src/main/java/examples/operations/switchapi/DeleteAnnouncementExample.java`](examples/src/main/java/examples/operations/switchapi/DeleteAnnouncementExample.java)

```java
client.announcements().deleteAnnouncement("123")
```


#### deleteBlockedNumber — `DELETE /blocked_numbers/delete`

Kara Listeden Silme

Java — [`examples/src/main/java/examples/operations/switchapi/DeleteBlockedNumberExample.java`](examples/src/main/java/examples/operations/switchapi/DeleteBlockedNumberExample.java)

```java
client.blacklist().deleteBlockedNumber("05111111111")
```


#### deleteContact — `DELETE /contacts/{id}`

Kişi Silme

Java — [`examples/src/main/java/examples/operations/switchapi/DeleteContactExample.java`](examples/src/main/java/examples/operations/switchapi/DeleteContactExample.java)

```java
client.contacts().deleteContact(1L)
```


#### deleteContactGroup — `DELETE /contact_groups/{id}`

Grup Silme

Java — [`examples/src/main/java/examples/operations/switchapi/DeleteContactGroupExample.java`](examples/src/main/java/examples/operations/switchapi/DeleteContactGroupExample.java)

```java
client.contacts().deleteContactGroup(1L)
```


#### deleteIvrCampaign — `DELETE /ivr_campaigns/{id}.json`

Otomatik Arama Kampanyasını Silme

Java — [`examples/src/main/java/examples/operations/switchapi/DeleteIvrCampaignExample.java`](examples/src/main/java/examples/operations/switchapi/DeleteIvrCampaignExample.java)

```java
client.ivrCampaigns().deleteIvrCampaign("123")
```


#### downloadFaxDocument — `GET /fax_document/{id}`

Faks Belgesi İndirme/Görüntüleme

Java — [`examples/src/main/java/examples/operations/switchapi/DownloadFaxDocumentExample.java`](examples/src/main/java/examples/operations/switchapi/DownloadFaxDocumentExample.java)

```java
client.fax().downloadFaxDocument("123")
```


#### getCdr — `GET /cdrs/{id}`

Belirli Bir Çağrının Detaylı CDR Kaydı

Java — [`examples/src/main/java/examples/operations/switchapi/GetCdrExample.java`](examples/src/main/java/examples/operations/switchapi/GetCdrExample.java)

```java
client.records().getCdr("call-uuid-12345-67890")
```


#### getCrmIntegrations — `GET /crm_integrations`

CRM Entegrasyon Ayarlarını Getirme

Java — [`examples/src/main/java/examples/operations/switchapi/GetCrmIntegrationsExample.java`](examples/src/main/java/examples/operations/switchapi/GetCrmIntegrationsExample.java)

```java
client.crm().getCrmIntegrations()
```


#### getExtension — `GET /extensions/{id}`

Dahili Detayı

Java — [`examples/src/main/java/examples/operations/switchapi/GetExtensionExample.java`](examples/src/main/java/examples/operations/switchapi/GetExtensionExample.java)

```java
client.users().getExtension("1001")
```


#### getWebhookPayloadExamples — `GET /webhook-payload-examples`

CRM Webhook Payload Örnekleri

Java — [`examples/src/main/java/examples/operations/switchapi/GetWebhookPayloadExamplesExample.java`](examples/src/main/java/examples/operations/switchapi/GetWebhookPayloadExamplesExample.java)

```java
client.crm().getWebhookPayloadExamples()
```


#### hangup — `GET /hangup/{id}`

Çağrıyı Sonlandırma

Java — [`examples/src/main/java/examples/operations/switchapi/HangupExample.java`](examples/src/main/java/examples/operations/switchapi/HangupExample.java)

```java
client.calls().hangup("f3797dfc-a818-11e7-bf70-cb295b6663ce")
```


#### listAgentStatuses — `GET /agent_statuses`

MT Durumlarını ve Üyeliklerini Listeleme

Java — [`examples/src/main/java/examples/operations/switchapi/ListAgentStatusesExample.java`](examples/src/main/java/examples/operations/switchapi/ListAgentStatusesExample.java)

```java
client.users().listAgentStatuses()
```


#### listAnnouncements — `GET /announcements`

Ses Dosyaları Listesine Erişim

Java — [`examples/src/main/java/examples/operations/switchapi/ListAnnouncementsExample.java`](examples/src/main/java/examples/operations/switchapi/ListAnnouncementsExample.java)

```java
client.announcements().listAnnouncements()
```


#### listBlockedNumbers — `GET /blocked_numbers`

Kara Listeye Erişim

Java — [`examples/src/main/java/examples/operations/switchapi/ListBlockedNumbersExample.java`](examples/src/main/java/examples/operations/switchapi/ListBlockedNumbersExample.java)

```java
client.blacklist().listBlockedNumbers()
```


#### listCallerIds — `GET /caller_ids`

Dış Numaralar Listesine Erişim

Java — [`examples/src/main/java/examples/operations/switchapi/ListCallerIdsExample.java`](examples/src/main/java/examples/operations/switchapi/ListCallerIdsExample.java)

```java
client.callerIds().listCallerIds()
```


#### listCdrs — `GET /cdrs`

Çağrı Detay Kayıtları (CDR) Listesi

Java — [`examples/src/main/java/examples/operations/switchapi/ListCdrsExample.java`](examples/src/main/java/examples/operations/switchapi/ListCdrsExample.java)

```java
client.records().listCdrs()
```


#### listContactGroups — `GET /contact_groups`

Grup Listesine Erişim

Java — [`examples/src/main/java/examples/operations/switchapi/ListContactGroupsExample.java`](examples/src/main/java/examples/operations/switchapi/ListContactGroupsExample.java)

```java
client.contacts().listContactGroups()
```


#### listContacts — `GET /contacts`

Kişiler Listesine Erişim

Java — [`examples/src/main/java/examples/operations/switchapi/ListContactsExample.java`](examples/src/main/java/examples/operations/switchapi/ListContactsExample.java)

```java
client.contacts().listContacts()
```


#### listExtensions — `GET /extensions`

Dahili Listesi

Java — [`examples/src/main/java/examples/operations/switchapi/ListExtensionsExample.java`](examples/src/main/java/examples/operations/switchapi/ListExtensionsExample.java)

```java
client.users().listExtensions()
```


#### listFaxOrders — `GET /fax_orders`

Tamamlanmamış Faks Gönderimlerinin Listesi

Java — [`examples/src/main/java/examples/operations/switchapi/ListFaxOrdersExample.java`](examples/src/main/java/examples/operations/switchapi/ListFaxOrdersExample.java)

```java
client.fax().listFaxOrders()
```


#### listFaxRecords — `GET /fdrs`

Faks Listesine Erişim

Java — [`examples/src/main/java/examples/operations/switchapi/ListFaxRecordsExample.java`](examples/src/main/java/examples/operations/switchapi/ListFaxRecordsExample.java)

```java
client.fax().listFaxRecords()
```


#### listQueuePendingCalls — `GET /queues/pending`

Kuyrukta Bekleyenler Listesine Erişim

Java — [`examples/src/main/java/examples/operations/switchapi/ListQueuePendingCallsExample.java`](examples/src/main/java/examples/operations/switchapi/ListQueuePendingCallsExample.java)

```java
client.queues().listQueuePendingCalls()
```


#### listQueueUsers — `GET /queue/user_list`

Kuyruktaki Dahili Listesine Erişim

Java — [`examples/src/main/java/examples/operations/switchapi/ListQueueUsersExample.java`](examples/src/main/java/examples/operations/switchapi/ListQueueUsersExample.java)

```java
client.queues().listQueueUsers("200")
```


#### listQueues — `GET /queues`

Kuyruklar Listesine Erişim

Java — [`examples/src/main/java/examples/operations/switchapi/ListQueuesExample.java`](examples/src/main/java/examples/operations/switchapi/ListQueuesExample.java)

```java
client.queues().listQueues()
```


#### listUserStatuses — `GET /user_statuses`

Dahili Durumlarını Listeleme

Java — [`examples/src/main/java/examples/operations/switchapi/ListUserStatusesExample.java`](examples/src/main/java/examples/operations/switchapi/ListUserStatusesExample.java)

```java
client.users().listUserStatuses()
```


#### listVoicemailMessages — `GET /voicemail_messages`

Telesekreter Arama Kayıtlarına Erişim

Java — [`examples/src/main/java/examples/operations/switchapi/ListVoicemailMessagesExample.java`](examples/src/main/java/examples/operations/switchapi/ListVoicemailMessagesExample.java)

```java
client.records().listVoicemailMessages()
```


#### manageQueueUsers — `GET /queue/manage_users`

Kuyruğa Dahili Ekleme, Çıkarma veya Yer Değiştirme

Java — [`examples/src/main/java/examples/operations/switchapi/ManageQueueUsersExample.java`](examples/src/main/java/examples/operations/switchapi/ManageQueueUsersExample.java)

```java
client.queues().manageQueueUsers("200", "1000,1001,1002")
```


#### originate — `POST /originate`

Çağrı Başlatma (POST)

Java — [`examples/src/main/java/examples/operations/switchapi/OriginateExample.java`](examples/src/main/java/examples/operations/switchapi/OriginateExample.java)

```java
client.calls().originate(
        new OriginateCallPostRequest()
            .extension("1001")
            .destination("908505320000"))
```


#### originateLegacy — `GET /originate`

Çağrı Başlatma (GET)

Java — [`examples/src/main/java/examples/operations/switchapi/OriginateLegacyExample.java`](examples/src/main/java/examples/operations/switchapi/OriginateLegacyExample.java)

```java
client.calls().originateLegacy("1001", "908505320000")
```


#### setCallMute — `GET /mute/{id}`

Çağrıyı Sessize Alma / Sesli Yapma

Java — [`examples/src/main/java/examples/operations/switchapi/SetCallMuteExample.java`](examples/src/main/java/examples/operations/switchapi/SetCallMuteExample.java)

```java
client.calls().setCallMute("f3797dfc-a818-11e7-bf70-cb295b6663ce", "on")
```


#### setDnd — `GET /dnd/{id}`

Dahili için Rahatsız Etme (DND) Modunu Ayarlama

Java — [`examples/src/main/java/examples/operations/switchapi/SetDndExample.java`](examples/src/main/java/examples/operations/switchapi/SetDndExample.java)

```java
client.users().setDnd("1001", "on")
```


#### transfer — `POST /transfer`

Çağrıyı Aktarma (POST)

Java — [`examples/src/main/java/examples/operations/switchapi/TransferExample.java`](examples/src/main/java/examples/operations/switchapi/TransferExample.java)

```java
client.calls().transfer("f3797dfc-a818-11e7-bf70-cb295b6663ce", "1000")
```


#### transferLegacy — `GET /transfer/{id}`

Çağrıyı Aktarma (GET)

Java — [`examples/src/main/java/examples/operations/switchapi/TransferLegacyExample.java`](examples/src/main/java/examples/operations/switchapi/TransferLegacyExample.java)

```java
client.calls().transferLegacy("f3797dfc-a818-11e7-bf70-cb295b6663ce", "1000")
```


#### updateAnnouncement — `PATCH /announcements/{id}`

Ses Dosyası Güncelleme

Java — [`examples/src/main/java/examples/operations/switchapi/UpdateAnnouncementExample.java`](examples/src/main/java/examples/operations/switchapi/UpdateAnnouncementExample.java)

```java
client.announcements().updateAnnouncement("123")
```


#### updateContact — `PATCH /contacts/{id}`

Kişi Güncelleme

Java — [`examples/src/main/java/examples/operations/switchapi/UpdateContactExample.java`](examples/src/main/java/examples/operations/switchapi/UpdateContactExample.java)

```java
client.contacts().updateContact(1L)
```


#### updateContactGroup — `PATCH /contact_groups/{id}`

Grup Güncelleme

Java — [`examples/src/main/java/examples/operations/switchapi/UpdateContactGroupExample.java`](examples/src/main/java/examples/operations/switchapi/UpdateContactGroupExample.java)

```java
client.contacts().updateContactGroup(1L, "Arkadaşlarım")
```


#### updateCrmIntegrations — `POST /crm_integrations`

CRM Entegrasyon Ayarlarını Güncelleme

Java — [`examples/src/main/java/examples/operations/switchapi/UpdateCrmIntegrationsExample.java`](examples/src/main/java/examples/operations/switchapi/UpdateCrmIntegrationsExample.java)

```java
client.crm().updateCrmIntegrations()
```


#### updateIvrCampaign — `PATCH /ivr_campaigns/{id}.json`

Otomatik Arama Kampanyasını Başlatma/Durdurma

Java — [`examples/src/main/java/examples/operations/switchapi/UpdateIvrCampaignExample.java`](examples/src/main/java/examples/operations/switchapi/UpdateIvrCampaignExample.java)

```java
client.ivrCampaigns().updateIvrCampaign("123", "on")
```


#### updateOutboundCallerId — `GET /update_outbound_caller_id`

Dahilinin Dış Numarasını (Arayan No) Değiştirme

Java — [`examples/src/main/java/examples/operations/switchapi/UpdateOutboundCallerIdExample.java`](examples/src/main/java/examples/operations/switchapi/UpdateOutboundCallerIdExample.java)

```java
client.callerIds().updateOutboundCallerId("1000", "90850532xxxx")
```


### WhatsApp

#### getMessage — `GET /v1/messages/{message_ref}`

Mesaj Kaydını Sorgula

Java — [`examples/src/main/java/examples/operations/whatsapp/GetMessageExample.java`](examples/src/main/java/examples/operations/whatsapp/GetMessageExample.java)

```java
client.messages().getMessage("3f2504e0-4f89-41d3-9a0c-0305e82c3301")
```


#### health — `GET /health`

Health check

Java — [`examples/src/main/java/examples/operations/whatsapp/HealthExample.java`](examples/src/main/java/examples/operations/whatsapp/HealthExample.java)

```java
client.health().health()
```


#### listMessages — `GET /v1/messages`

Mesajları Listele / Ara

Java — [`examples/src/main/java/examples/operations/whatsapp/ListMessagesExample.java`](examples/src/main/java/examples/operations/whatsapp/ListMessagesExample.java)

```java
client.messages().listMessages()
```


#### sendBulk — `POST /v1/messages/bulk`

Toplu Şablon Mesajı Gönder

Java — [`examples/src/main/java/examples/operations/whatsapp/SendBulkExample.java`](examples/src/main/java/examples/operations/whatsapp/SendBulkExample.java)

```java
client.messages().sendBulk(
        new BulkMessageRequest()
            .templateName("odeme_hatirlatici")
            .recipients(List.of(
                new BulkRecipient()
                    .to("905001112233"))))
```


#### sendOtp — `POST /v1/messages/otp`

OTP / Kimlik Doğrulama Mesajı Gönder

Java — [`examples/src/main/java/examples/operations/whatsapp/SendOtpExample.java`](examples/src/main/java/examples/operations/whatsapp/SendOtpExample.java)

```java
client.messages().sendOtp(
        new TemplateMessageRequest()
            .to("905001112233")
            .templateName("siparis_onay"))
```


#### sendUtility — `POST /v1/messages/utility`

Utility / İşlemsel Mesaj Gönder

Java — [`examples/src/main/java/examples/operations/whatsapp/SendUtilityExample.java`](examples/src/main/java/examples/operations/whatsapp/SendUtilityExample.java)

```java
client.messages().sendUtility(
        new TemplateMessageRequest()
            .to("905001112233")
            .templateName("siparis_onay"))
```
