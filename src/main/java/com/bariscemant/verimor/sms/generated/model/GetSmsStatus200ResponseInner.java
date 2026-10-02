/*
 * Verimor SMS API
 * <p>Verimor SMS API, uygulamalarınız veya sunucu taraflı yazılımlarınız üzerinden SMS gönderimi ve yönetimi yapmanızı sağlayan bir HTTP arayüzüdür. API, farklı amaçlara yönelik (toplu gönderim, raporlama, bakiye sorgulama vb.) çeşitli endpoint'ler sunar.</p>  <h3>Kimlik Doğrulama (Authentication)</h3> <p>API'ye yapılan istekler, Verimor kullanıcı adı ve API şifreniz ile doğrulanır. Kimlik doğrulama yöntemi, isteğin türüne göre değişir:</p> <ul> <li><strong>POST İstekleri (örn: /v2/send.json):</strong> <code>username</code> ve <code>password</code> bilgileri, isteğin gövdesinde (request body) JSON formatında gönderilir.</li> <li><strong>GET İstekleri (örn: /v2/report):</strong> <code>username</code> ve <code>password</code> bilgileri, isteğin URL'ine query string parametresi olarak eklenir.</li> </ul> <p>API şifrenizi Verimor Online İşlem Merkezi (OİM) üzerinden oluşturabilirsiniz.</p>  <h3>Temel Yetenekler</h3> <p>API, aşağıdaki temel işlevleri desteklemektedir:</p> <ul> <li>Tekil veya toplu SMS gönderimi</li> <li>İleri tarihli SMS gönderimlerini programlama</li> <li>Gönderilen mesajların iletim durumlarını detaylı olarak sorgulama</li> <li>Hesapta kalan SMS kredisini öğrenme</li> <li>Zamanlanmış gönderimleri iptal etme</li> <li>Onaylanmış gönderici başlıklarını (alfanümerik) listeleme</li> </ul>  <h3>Teknik Formatlar</h3> <p>API, operasyona göre farklı veri formatları kullanır. Mesaj gönderme gibi <strong>POST</strong> işlemleri <code>application/json</code> formatında veri kabul eder ve yanıt döner. Raporlama gibi <strong>GET</strong> işlemleri ise parametreleri URL üzerinden alır ve yanıtı, isteğe bağlı olarak, varsayılan olarak <strong>boşluklarla ayrılmış düz metin (plain text)</strong> veya belirtilirse <strong>JSON</strong> formatında döndürebilir.</p>  <h3>Genel Notlar</h3> <ul> <li>/v2/send ve /v2/iys_consents.json aynı hız sınırı havuzunu paylaşır: dakikada toplam 240 istek gönderebilirsiniz (burst 80). 1 isteğin büyüklüğü 10 MB geçemez. Bu limitler dahilinde, isteğin yapısına bağlı olmakla birlikte dakikada 100.000.000 mesaj gönderilebilir.</li> <li>Yoğun OTP gönderimleri için kendi tarafınızda istekleri biriktirip saniyede bir post yöntemiyle sms gönderim isteği (çok kişiye çok mesaj isteği) yapmalısınız.</li> <li>Request limitlerini aştığınızda 429 (Too Many Requests) hatası döner.</li> <li>Paket boyutu limitini aştığınızda 413 (Request Entity Too Large) hatası döner.</li> <li>/v2/status, /v2/balance, /v2/cancel, /v2/headers, /v2/blacklists, /v2/inbound_messages ve /v2/iys/campaigns endpoint'leri kendi aralarında aynı hız sınırı havuzunu paylaşır: dakikada toplam 20 istek gönderebilirsiniz (burst 10). Önerimiz Push yöntemini kullanmanızdır.</li> <li>HTTPS olarak API'mizi kullanırken SSL bağlantısı için kullandığınız kütüphane sisteminizde kök sertifikalar yüklü olmadığından sertifikamızı doğrulamayabilir. Bu sorunu çözmek için lets-encrypt-r3.crt kök sertifika dosyasını <a href=\"https://github.com/verimor/SMS-API/blob/master/lets-encrypt-r3.crt\">buraya</a> tıklayarak indirip sisteminize kurmalısınız.</li> <li>Mesaj metninde yeni satıra geçiş yapabilmek için json'da (new line) \"\\n\" kullanımı gerekmektedir.</li> </ul>  <h3>Hata Kodları</h3> <p>SMS gönderirken ve gönderim raporu alırken size dönen status sahalarında aşağıdaki tablodaki değerler olabilir:</p> <p><strong>Mesaj Gönderirken Dönebilecek Durumlar ve Açıklamaları</strong></p> <table> <thead><tr><th>Web_Arayüzü_Durumları</th><th>API</th><th>Açıklama</th></tr></thead> <tbody> <tr><td>-</td><td>INVALID_SOURCE_ADDRESS</td><td>Başlık kabul edilmedi.</td></tr> <tr><td>-</td><td>MISSING_MESSAGE</td><td>Gönderilecek mesaj verilmemiş.</td></tr> <tr><td>-</td><td>MESSAGE_TOO_LONG</td><td>Mesaj çok uzun.</td></tr> <tr><td>-</td><td>INVALID_PERIOD</td><td>Mesajın geçerlilik süresi (validity period) geçersiz. (1dk. ile 48 saat arasında değil).</td></tr> <tr><td>-</td><td>INVALID_DELIVERY_TIME</td><td>\"send_at\" parametresi geçersiz veya geçmiş tarihe ait.</td></tr> <tr><td>-</td><td>INVALID_DATACODING</td><td>datacoding parametresi hatalı verilmiş.</td></tr> <tr><td>-</td><td>MISSING_IYS_BRAND_CODE</td><td>Ticari gönderimlerde başlığın marka kodunun tanımlanmış olması gereklidir</td></tr> <tr><td>-</td><td>AHS_AUTHORIZATION_ERROR</td><td>Yetkilendirme hatası. Lütfen İYS ile iletişime geçip Verimor'a AHS izni veriniz.</td></tr> <tr><td>-</td><td>NO_AHS_BRAND_ERROR</td><td>VKN'ye ait, İYS'de kayıtlı bir marka bulunamadı.</td></tr> <tr><td>-</td><td>COMMERCIAL_SENDING_ERROR_UNDER_150K</td><td>150 bin adedin altında ticari elektronik ileti onayı olan hesaplar için ticari gönderim 16 Temmuz 2021'de başlayacaktır. Bu tarihe kadar normal gönderimi kullanmalısınız.</td></tr> <tr><td>-</td><td>INVALID_IYS_RECIPIENT_TYPE</td><td>iys_recipient_type \"BIREYSEL\" yada \"TACIR\" olmalıdır.</td></tr> <tr><td>-</td><td>MISSING_DESTINATION_ADDRESS</td><td>Mesaj için alıcı verilmemiş.</td></tr> <tr><td>Hatalı Numara</td><td>INVALID_DESTINATION_ADDRESS</td><td>Alıcı telefon numarasının formatı geçersiz. (905121234567 gibi olmalı)</td></tr> <tr><td>-</td><td>INVALID_UTF8</td><td>Encoding UTF8 olmalıdır.</td></tr> <tr><td>-</td><td>MUKERRER_RAPORLAMA</td><td>24 Saat içerisinde aynı sms zaten atılmış.</td></tr> <tr><td>Kredi Yetersiz</td><td>INSUFFICIENT_CREDITS</td><td>Mesajı göndermek için yeterli bakiyeniz yok.</td></tr> <tr><td>Yasaklı içerik</td><td>FORBIDDEN_MESSAGE</td><td>Mesajınız yasak kelime(ler) içeriyor.</td></tr> <tr><td>-</td><td>INVALID_CONSENT_DATE</td><td>\"consent_date\" 1 Mayıs 2015 tarihinden önce olamaz.<br>\"consent_date\" ileri bir tarih olamaz.<br>\"consent_date\" 3 günden eski olamaz.<br>Kaynağı HS_2015 olan izinlerde \"consent_date\" 1 Mayıs 2015 olmalıdır.</td></tr> <tr><td>-</td><td>MISSING_CONSENT</td><td>Eksik izin durumu.</td></tr> <tr><td>-</td><td>MISSING_CONSENT_DATE</td><td>Gönderim tipi \"BIREYSEL\" olanlarda consent_date girilmelidir.</td></tr> <tr><td>-</td><td>INVALID_RECIPIENT</td><td>Geçersiz gönderim tipi.</td></tr> <tr><td>-</td><td>INVALID_JSON</td><td>Geçersiz JSON kullanımı</td></tr> <tr><td>-</td><td>MESSAGE_COUNT_LIMIT_EXCEEDED</td><td>Maksimum mesaj sayısına ulaşıldı. Bir seferde maksimum 50.000 adet mesajdan daha fazlası kabul edilmez.</td></tr> <tr><td>-</td><td>MULTIPLE_DESTINATION_NOT_ALLOWED</td><td>Yalnızca /v2/otp: dest alanında birden fazla (virgülle ayrılmış) numara gönderildi. /v2/otp yalnızca tek alıcıyı destekler.</td></tr> <tr><td>-</td><td>MISSING_CODE_OR_MESSAGE</td><td>Yalnızca /v2/otp: code ve msg alanlarının ikisi de boş.</td></tr> <tr><td>-</td><td>MISSING_CODE</td><td>Yalnızca /v2/otp: msg içinde {code} yer tutucusu var ama code gönderilmemiş.</td></tr> </tbody> </table> <p><strong>Mesaj Durumu Alınırken Dönebilecek Durumlar ve Açıklamaları</strong></p> <table> <thead><tr><th>Web_Arayüzü_Durumları</th><th>API_Durumları</th><th>Açıklama</th></tr></thead> <tbody> <tr><td>Gönderiliyor</td><td>SENDING</td><td>Mesaj gönderiliyor.</td></tr> <tr><td>Bekliyor</td><td>WAITING</td><td>Mesaj gönderildi. Cevap bekleniyor.</td></tr> <tr><td>İletildi</td><td>DELIVERED</td><td>Mesaj iletildi.</td></tr> <tr><td>İletildi</td><td>SENT</td><td>Mesaj iletildi. Fakat operatör gönderim raporunu desteklemediği için teyit edilemiyor. (Uluslararası bazı yönlerde oluşur.)</td></tr> <tr><td>İletilemedi</td><td>NOT_DELIVERED</td><td>Mesaj iletilemedi. (Genelde alıcı numaranın aktif olmamasından kaynaklanır.)</td></tr> <tr><td>Zaman aşımı</td><td>EXPIRED</td><td>Zaman aşımı. Mesajınız belirlediğiniz geçerlilik süresi içinde alıcısına teslim edilemedi.</td></tr> <tr><td>Hatalı Numara</td><td>INVALID_DESTINATION_ADDRESS</td><td>Alıcı telefon numarası geçersiz. (Hiçbir operatöre kayıtlı değil.)</td></tr> <tr><td>Reddedildi</td><td>REJECTED</td><td>Mesajınızın gönderimi reddedildi. (Genelde gsm operatörü tarafından içerik kontrolü sonucu oluşur.)</td></tr> <tr><td>Mükerrer Gönderim</td><td>DOUBLE_SEND_ERROR</td><td>Aynı içerik aynı gün aynı başlıkla aynı numaraya gönderilmiş. Mükerrer gönderim engellendi.</td></tr> <tr><td>Karalistede</td><td>BLACKLISTED_DESTINATION_ADDRESS</td><td>Alıcı kara listenizde.</td></tr> <tr><td>İYS izni yok</td><td>NOT_ALLOWED_BY_IYS</td><td>İYS izni yok.</td></tr> <tr><td>Tarife Bulunamadı</td><td>MISSING_TARIFF</td><td>Alıcının operatörü tarifelerimiz arasında bulunamamıştır. (Uluslararası yönlerde oluşur.)</td></tr> <tr><td>Geçersiz Şebeke</td><td>ROUTE_NOT_AVAILABLE</td><td>Hesabınız bu alıcıya mesaj gönderemez. (Uluslararası bazı yönlerde oluşur.)</td></tr> <tr><td>Geçersiz Şebeke</td><td>NETWORK_NOTCOVERED</td><td>Hesabınız bu alıcıya mesaj gönderemez. (Uluslararası bazı yönlerde oluşur.)</td></tr> <tr><td>Gönderim Hatası</td><td>SEND_ERROR</td><td>Mesajınız gönderilirken hata oluştu. (Sebebi çeşitli olabilir.)</td></tr> <tr><td>Uluslararası Gönderim Kapalı</td><td>INTERNATIONAL_DENIED</td><td>OİM'de SMS ayarlarından \"uluslararası gönderim\" ayarı kapalı olduğu için gönderilmedi.</td></tr> </tbody> </table> <p><strong>Mesaj Hata Kodları (gsm_error)</strong><br>İletilemeyen mesajlar için karşı operatörden alınan teknik hata kodları ve açıklamaları aşağıda verilmiştir.</p> <table> <thead><tr><th>Hata No</th><th>Hata Kodu</th><th>Açıklama</th></tr></thead> <tbody> <tr><td>1</td><td>EC_UNKNOWN_SUBSCRIBER</td><td>Numara karşı operatörün veritabanında bir aboneye tanımlı değil</td></tr> <tr><td>6</td><td>EC_ABSENT_SUBSCRIBER_SM</td><td>Karşı aboneden sinyal alınamadı. Abonenin telefonunun kapalı olduğu durumda veya sinyalin zayıf olduğu durumda görülür</td></tr> <tr><td>11</td><td>EC_TELESERVICE_NOT_PROVISIONED</td><td>Karşı abonenin mobil hizmeti operatörü tarafından durduruldu</td></tr> <tr><td>13</td><td>EC_CALL_BARRED</td><td>Karşı abone \"Rahatsız Etme\" (DND) hizmetini açtı, hiç mesaj almamayı tercih etti</td></tr> <tr><td>27</td><td>EC_ABSENT_SUBSCRIBER</td><td>Karşı abone çevrimiçi değil, telefon cihazı tarafından teyit edildi. Telefon kapatılınca görülür.</td></tr> <tr><td>31</td><td>EC_SUBSCRIBER_BUSY_FOR_MT_SMS</td><td>Karşı operatör fazla trafikten dolayı meşgul olduğunu bildirdi</td></tr> <tr><td>32</td><td>EC_SM_DELIVERY_FAILURE</td><td>Karşı operatör kısa mesajı abonesine iletemediğini bildirdi</td></tr> <tr><td>34</td><td>EC_SYSTEM_FAILURE</td><td>Karşı operatör sistem hatası bildirdi</td></tr> <tr><td>256</td><td>EC_SM_DF_MEMORYCAPACITYEXCEEDED</td><td>Karşı abonenin telefon cihazında mesajı kaydedecek yer kalmadı</td></tr> <tr><td>257</td><td>EC_SM_DF_EQUIPMENTPROTOCOLERROR</td><td>Karşı operatör, abonenin telefon cihazında hata olduğunu bildirdi</td></tr> <tr><td>258</td><td>EC_SM_DF_EQUIPMENTNOTSM_EQUIPPED</td><td>Karşı operatör, abonenin telefon cihazında hata olduğunu bildirdi</td></tr> <tr><td>500</td><td>EC_PROVIDER_GENERAL_ERROR</td><td>Karşı operatör genel hata bildirdi</td></tr> <tr><td>502</td><td>EC_NO_RESPONSE</td><td>Mesaj karşı operatöre iletildi fakat olumlu veya olumsuz bir iletim raporu dönmedi</td></tr> <tr><td>1030</td><td>EC_OR_POTENTIALVERSIONINCOMPATIBILITY</td><td>Karşı operatör genel hata bildirdi</td></tr> <tr><td>1155</td><td>EC_NNR_SUBSYSTEMFAILURE</td><td>Karşı operatör, sistem hatasından dolayı abonesine ulaşamadığını bildirdi</td></tr> <tr><td>1157</td><td>EC_NNR_MTPFAILURE</td><td>Karşı operatör genel hata bildirdi</td></tr> <tr><td>1281</td><td>EC_UA_USERSPECIFICREASON</td><td>Karşı operatör genel hata bildirdi</td></tr> <tr><td>1536</td><td>EC_PA_PROVIDERMALFUNCTION</td><td>Karşı operatör genel hata bildirdi</td></tr> <tr><td>2048</td><td>EC_TIME_OUT</td><td>Mesaj karşı operatöre geçerlilik süresi içinde iletilemedi</td></tr> <tr><td>2049</td><td>EC_IMSI_BLACKLISTED</td><td>Karşı abonenin SIM kartı operatörünün karalistesinde</td></tr> <tr><td>2050</td><td>EC_DEST_ADDRESS_BLACKLISTED</td><td>Numara karalistemizde olduğu için iletilemedi</td></tr> <tr><td>2051</td><td>EC_INVALIDMSCADDRESS</td><td>Mesaj metni karalistemizde olduğu için iletilemedi</td></tr> <tr><td>2053</td><td>EC_BLACKLISTED_SENDERADDRESS</td><td>Mesaj başlığının kullanımı için ek onay alınması gerekli</td></tr> <tr><td>4100</td><td>EC_MESSAGE_CANCELED</td><td>Karşı operatör mesajı abonesine geçerlilik süresi içinde iletemedi</td></tr> <tr><td>4101</td><td>EC_VALIDITYEXPIRED</td><td>Karşı operatör mesajı abonesine geçerlilik süresi içinde iletemedi</td></tr> <tr><td>4103</td><td>EC_DESTINATION_FLOODING</td><td>Karşıdaki abone çok fazla mesaj almış olduğu için yeni mesaj kabul etmiyor</td></tr> <tr><td>4104</td><td>EC_DESTINATION_TXT_FLOODING</td><td>Karşıdaki aboneye aynı mesaj çok defa gönderilmiş olduğu için yeni mesaj kabul etmiyor</td></tr> </tbody> </table>  <h3>SMS Boy Karakter Limitleri</h3> <table> <thead><tr><th></th><th>Normal (datacoding=0)</th><th>Türkçe (datacoding=1)</th><th>Unicode (datacoding=2)</th></tr></thead> <tbody> <tr><td>1 boy</td><td>0-160</td><td>0-155</td><td>0-70</td></tr> <tr><td>2 boy</td><td>161-306</td><td>156-298</td><td>71-134</td></tr> <tr><td>3 boy</td><td>307-459</td><td>299-447</td><td>135-201</td></tr> <tr><td>4 boy</td><td>460-612</td><td>448-596</td><td>202-268</td></tr> <tr><td>5 boy</td><td>613-765</td><td>597-745</td><td>269-335</td></tr> <tr><td>6 boy</td><td>766-918</td><td>746-894</td><td>336-402</td></tr> <tr><td>7 boy</td><td>919-1071</td><td>895-1043</td><td>403-469</td></tr> </tbody> </table> <p><strong>Not-1:</strong> datacoding=0 veya datacoding=1 gönderimlerde aşağıdaki karakterler 2 karakter sayılır. ^ { } \\ [ ] ~ | €<br><strong>Not-2:</strong> Sadece (Ş ş Ğ ğ ç ı İ) harfleri Türkçe olarak kabul edilir ve datacoding=1 olarak gönderilmelidir. Diğer Türkçe karakterleri (Ö ö Ü ü Ç) datacoding=0 olarak gönderebilirsiniz.</p>
 *
 * The version of the OpenAPI document: v2
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package com.bariscemant.verimor.sms.generated.model;

import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * GetSmsStatus200ResponseInner
 */
@JsonPropertyOrder({
  GetSmsStatus200ResponseInner.JSON_PROPERTY_CAMPAIGN_ID,
  GetSmsStatus200ResponseInner.JSON_PROPERTY_CAMPAIGN_CUSTOM_ID,
  GetSmsStatus200ResponseInner.JSON_PROPERTY_MESSAGE_ID,
  GetSmsStatus200ResponseInner.JSON_PROPERTY_MESSAGE_CUSTOM_ID,
  GetSmsStatus200ResponseInner.JSON_PROPERTY_DEST,
  GetSmsStatus200ResponseInner.JSON_PROPERTY_SIZE,
  GetSmsStatus200ResponseInner.JSON_PROPERTY_INTERNATIONAL_MULTIPLIER,
  GetSmsStatus200ResponseInner.JSON_PROPERTY_CREDITS,
  GetSmsStatus200ResponseInner.JSON_PROPERTY_STATUS,
  GetSmsStatus200ResponseInner.JSON_PROPERTY_GSM_ERROR,
  GetSmsStatus200ResponseInner.JSON_PROPERTY_SENT_AT,
  GetSmsStatus200ResponseInner.JSON_PROPERTY_DONE_AT
})
public class GetSmsStatus200ResponseInner {
  public static final String JSON_PROPERTY_CAMPAIGN_ID = "campaign_id";
  @javax.annotation.Nullable
  private Integer campaignId;

  public static final String JSON_PROPERTY_CAMPAIGN_CUSTOM_ID = "campaign_custom_id";
  @javax.annotation.Nullable
  private String campaignCustomId;

  public static final String JSON_PROPERTY_MESSAGE_ID = "message_id";
  @javax.annotation.Nullable
  private Integer messageId;

  public static final String JSON_PROPERTY_MESSAGE_CUSTOM_ID = "message_custom_id";
  @javax.annotation.Nullable
  private String messageCustomId;

  public static final String JSON_PROPERTY_DEST = "dest";
  @javax.annotation.Nullable
  private String dest;

  public static final String JSON_PROPERTY_SIZE = "size";
  @javax.annotation.Nullable
  private Integer size;

  public static final String JSON_PROPERTY_INTERNATIONAL_MULTIPLIER = "international_multiplier";
  @javax.annotation.Nullable
  private Integer internationalMultiplier;

  public static final String JSON_PROPERTY_CREDITS = "credits";
  @javax.annotation.Nullable
  private Integer credits;

  public static final String JSON_PROPERTY_STATUS = "status";
  @javax.annotation.Nullable
  private String status;

  public static final String JSON_PROPERTY_GSM_ERROR = "gsm_error";
  @javax.annotation.Nullable
  private String gsmError;

  public static final String JSON_PROPERTY_SENT_AT = "sent_at";
  @javax.annotation.Nullable
  private OffsetDateTime sentAt;

  public static final String JSON_PROPERTY_DONE_AT = "done_at";
  @javax.annotation.Nullable
  private OffsetDateTime doneAt;

  public GetSmsStatus200ResponseInner() { 
  }

  public GetSmsStatus200ResponseInner campaignId(@javax.annotation.Nullable Integer campaignId) {
    this.campaignId = campaignId;
    return this;
  }

  /**
   * Get campaignId
   * @return campaignId
   */
  @javax.annotation.Nullable
  @JsonProperty(value = JSON_PROPERTY_CAMPAIGN_ID, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Integer getCampaignId() {
    return campaignId;
  }


  @JsonProperty(value = JSON_PROPERTY_CAMPAIGN_ID, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCampaignId(@javax.annotation.Nullable Integer campaignId) {
    this.campaignId = campaignId;
  }


  public GetSmsStatus200ResponseInner campaignCustomId(@javax.annotation.Nullable String campaignCustomId) {
    this.campaignCustomId = campaignCustomId;
    return this;
  }

  /**
   * Get campaignCustomId
   * @return campaignCustomId
   */
  @javax.annotation.Nullable
  @JsonProperty(value = JSON_PROPERTY_CAMPAIGN_CUSTOM_ID, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getCampaignCustomId() {
    return campaignCustomId;
  }


  @JsonProperty(value = JSON_PROPERTY_CAMPAIGN_CUSTOM_ID, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCampaignCustomId(@javax.annotation.Nullable String campaignCustomId) {
    this.campaignCustomId = campaignCustomId;
  }


  public GetSmsStatus200ResponseInner messageId(@javax.annotation.Nullable Integer messageId) {
    this.messageId = messageId;
    return this;
  }

  /**
   * Get messageId
   * @return messageId
   */
  @javax.annotation.Nullable
  @JsonProperty(value = JSON_PROPERTY_MESSAGE_ID, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Integer getMessageId() {
    return messageId;
  }


  @JsonProperty(value = JSON_PROPERTY_MESSAGE_ID, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setMessageId(@javax.annotation.Nullable Integer messageId) {
    this.messageId = messageId;
  }


  public GetSmsStatus200ResponseInner messageCustomId(@javax.annotation.Nullable String messageCustomId) {
    this.messageCustomId = messageCustomId;
    return this;
  }

  /**
   * Get messageCustomId
   * @return messageCustomId
   */
  @javax.annotation.Nullable
  @JsonProperty(value = JSON_PROPERTY_MESSAGE_CUSTOM_ID, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getMessageCustomId() {
    return messageCustomId;
  }


  @JsonProperty(value = JSON_PROPERTY_MESSAGE_CUSTOM_ID, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setMessageCustomId(@javax.annotation.Nullable String messageCustomId) {
    this.messageCustomId = messageCustomId;
  }


  public GetSmsStatus200ResponseInner dest(@javax.annotation.Nullable String dest) {
    this.dest = dest;
    return this;
  }

  /**
   * Get dest
   * @return dest
   */
  @javax.annotation.Nullable
  @JsonProperty(value = JSON_PROPERTY_DEST, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getDest() {
    return dest;
  }


  @JsonProperty(value = JSON_PROPERTY_DEST, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDest(@javax.annotation.Nullable String dest) {
    this.dest = dest;
  }


  public GetSmsStatus200ResponseInner size(@javax.annotation.Nullable Integer size) {
    this.size = size;
    return this;
  }

  /**
   * Get size
   * @return size
   */
  @javax.annotation.Nullable
  @JsonProperty(value = JSON_PROPERTY_SIZE, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Integer getSize() {
    return size;
  }


  @JsonProperty(value = JSON_PROPERTY_SIZE, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSize(@javax.annotation.Nullable Integer size) {
    this.size = size;
  }


  public GetSmsStatus200ResponseInner internationalMultiplier(@javax.annotation.Nullable Integer internationalMultiplier) {
    this.internationalMultiplier = internationalMultiplier;
    return this;
  }

  /**
   * Get internationalMultiplier
   * @return internationalMultiplier
   */
  @javax.annotation.Nullable
  @JsonProperty(value = JSON_PROPERTY_INTERNATIONAL_MULTIPLIER, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Integer getInternationalMultiplier() {
    return internationalMultiplier;
  }


  @JsonProperty(value = JSON_PROPERTY_INTERNATIONAL_MULTIPLIER, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setInternationalMultiplier(@javax.annotation.Nullable Integer internationalMultiplier) {
    this.internationalMultiplier = internationalMultiplier;
  }


  public GetSmsStatus200ResponseInner credits(@javax.annotation.Nullable Integer credits) {
    this.credits = credits;
    return this;
  }

  /**
   * Get credits
   * @return credits
   */
  @javax.annotation.Nullable
  @JsonProperty(value = JSON_PROPERTY_CREDITS, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Integer getCredits() {
    return credits;
  }


  @JsonProperty(value = JSON_PROPERTY_CREDITS, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCredits(@javax.annotation.Nullable Integer credits) {
    this.credits = credits;
  }


  public GetSmsStatus200ResponseInner status(@javax.annotation.Nullable String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  @javax.annotation.Nullable
  @JsonProperty(value = JSON_PROPERTY_STATUS, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getStatus() {
    return status;
  }


  @JsonProperty(value = JSON_PROPERTY_STATUS, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setStatus(@javax.annotation.Nullable String status) {
    this.status = status;
  }


  public GetSmsStatus200ResponseInner gsmError(@javax.annotation.Nullable String gsmError) {
    this.gsmError = gsmError;
    return this;
  }

  /**
   * Get gsmError
   * @return gsmError
   */
  @javax.annotation.Nullable
  @JsonProperty(value = JSON_PROPERTY_GSM_ERROR, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getGsmError() {
    return gsmError;
  }


  @JsonProperty(value = JSON_PROPERTY_GSM_ERROR, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setGsmError(@javax.annotation.Nullable String gsmError) {
    this.gsmError = gsmError;
  }


  public GetSmsStatus200ResponseInner sentAt(@javax.annotation.Nullable OffsetDateTime sentAt) {
    this.sentAt = sentAt;
    return this;
  }

  /**
   * Get sentAt
   * @return sentAt
   */
  @javax.annotation.Nullable
  @JsonProperty(value = JSON_PROPERTY_SENT_AT, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public OffsetDateTime getSentAt() {
    return sentAt;
  }


  @JsonProperty(value = JSON_PROPERTY_SENT_AT, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSentAt(@javax.annotation.Nullable OffsetDateTime sentAt) {
    this.sentAt = sentAt;
  }


  public GetSmsStatus200ResponseInner doneAt(@javax.annotation.Nullable OffsetDateTime doneAt) {
    this.doneAt = doneAt;
    return this;
  }

  /**
   * Get doneAt
   * @return doneAt
   */
  @javax.annotation.Nullable
  @JsonProperty(value = JSON_PROPERTY_DONE_AT, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public OffsetDateTime getDoneAt() {
    return doneAt;
  }


  @JsonProperty(value = JSON_PROPERTY_DONE_AT, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDoneAt(@javax.annotation.Nullable OffsetDateTime doneAt) {
    this.doneAt = doneAt;
  }


  /**
   * Return true if this getSmsStatus_200_response_inner object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetSmsStatus200ResponseInner getSmsStatus200ResponseInner = (GetSmsStatus200ResponseInner) o;
    return Objects.equals(this.campaignId, getSmsStatus200ResponseInner.campaignId) &&
        Objects.equals(this.campaignCustomId, getSmsStatus200ResponseInner.campaignCustomId) &&
        Objects.equals(this.messageId, getSmsStatus200ResponseInner.messageId) &&
        Objects.equals(this.messageCustomId, getSmsStatus200ResponseInner.messageCustomId) &&
        Objects.equals(this.dest, getSmsStatus200ResponseInner.dest) &&
        Objects.equals(this.size, getSmsStatus200ResponseInner.size) &&
        Objects.equals(this.internationalMultiplier, getSmsStatus200ResponseInner.internationalMultiplier) &&
        Objects.equals(this.credits, getSmsStatus200ResponseInner.credits) &&
        Objects.equals(this.status, getSmsStatus200ResponseInner.status) &&
        Objects.equals(this.gsmError, getSmsStatus200ResponseInner.gsmError) &&
        Objects.equals(this.sentAt, getSmsStatus200ResponseInner.sentAt) &&
        Objects.equals(this.doneAt, getSmsStatus200ResponseInner.doneAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(campaignId, campaignCustomId, messageId, messageCustomId, dest, size, internationalMultiplier, credits, status, gsmError, sentAt, doneAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetSmsStatus200ResponseInner {\n");
    sb.append("    campaignId: ").append(toIndentedString(campaignId)).append("\n");
    sb.append("    campaignCustomId: ").append(toIndentedString(campaignCustomId)).append("\n");
    sb.append("    messageId: ").append(toIndentedString(messageId)).append("\n");
    sb.append("    messageCustomId: ").append(toIndentedString(messageCustomId)).append("\n");
    sb.append("    dest: ").append(toIndentedString(dest)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    internationalMultiplier: ").append(toIndentedString(internationalMultiplier)).append("\n");
    sb.append("    credits: ").append(toIndentedString(credits)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    gsmError: ").append(toIndentedString(gsmError)).append("\n");
    sb.append("    sentAt: ").append(toIndentedString(sentAt)).append("\n");
    sb.append("    doneAt: ").append(toIndentedString(doneAt)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

