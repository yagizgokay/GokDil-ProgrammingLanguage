# GökDil - Türkçe Programlama Dili

<img src="src/resources/logo.png" width="200" height="200" alt="GökDil Logo">

GökDil, Türkçe sözdizimi ile programlama yapmayı sağlayan, eğitim amaçlı geliştirilmiş bir programlama dilidir. Temel programlama kavramlarını Türkçe ifadelerle öğretmeyi amaçlar.

## Özellikler

### 1. Türkçe Anahtar Kelimeler
- `tanımla`: Değişken tanımlama
- `tür`: Değişken türü belirtme
- `başlangıç`: Değişken başlangıç değeri atama
- `ise`: Koşul ifadeleri
- `değilse`: Alternatif koşul
- `böyleyken`: Döngü yapısı
- `yazdır`: Ekrana çıktı verme
- `fonksiyon`: Fonksiyon tanımlama
- `dön`: Fonksiyondan değer döndürme

### 2. Veri Tipleri
- `tamsayı`: Tam sayı değerler
- `ondalık`: Ondalıklı sayılar
- `yazı`: Metin değerleri

### 3. Operatörler
- Aritmetik: `+`, `-`, `*`, `/`, `%`
- Karşılaştırma: `>`, `<`, `>=`, `<=`, `==`
- Mantıksal: `ve`, `veya`
- Atama: `=`

### 4. Kontrol Yapıları
```
ise (koşul) {
    // kod bloğu
} değilse {
    // kod bloğu
}

böyleyken (koşul) {
    // döngü bloğu
}
```

### 5. Fonksiyonlar
```
fonksiyon topla(a, b) {
    dön a + b$
}
```

## Örnek Kod

```
@ Değişken tanımlama
tanımla sayi1 tür tamsayı başlangıç 42$
tanımla metin tür yazı başlangıç "Merhaba GökDil"$

@ Koşullu ifade
ise (sayi1 > 40) {
    yazdır("sayi1 40'tan büyük")$
} değilse {
    yazdır("sayi1 40'tan küçük veya eşit")$
}

@ Döngü örneği
tanımla sayac tür tamsayı başlangıç 0$
böyleyken (sayac < 3) {
    yazdır(sayac)$
    sayac = sayac + 1$
}
```

## Özel Sözdizimi Özellikleri

1. Her ifade sonunda `$` işareti kullanılır
2. Yorum satırları `@` işareti ile başlar
3. Metin değerleri çift tırnak içinde yazılır
4. Bloklar süslü parantez `{}` içinde tanımlanır

## Kullanım

1. Test dosyalarınızı `.txt` uzantısı ile kaydedin
2. GökDil GUI uygulamasını başlatın
3. "Test Dosyası Seç" butonuna tıklayın
4. Çalıştırmak istediğiniz dosyayı seçin

## Geliştirme Ortamı

GökDil, Java programlama dili kullanılarak geliştirilmiştir. Temel bileşenleri:

- Lexer: Kaynak kodu tokenlara ayırır
- Parser: Tokenleri sözdizimi ağacına dönüştürür
- Interpreter: Sözdizimi ağacını yorumlayarak kodu çalıştırır
- GUI: Kullanıcı dostu grafiksel arayüz

## Test Dosyaları

- `dogru1.txt`: Temel özelliklerin örnekleri
- `dogru2.txt`: İleri seviye özellikler
- `hatali1.txt` ve `hatali2.txt`: Hata örnekleri

## Hata Yönetimi

GökDil, programlama hatalarını Türkçe olarak bildirir:
- Sözdizimi hataları
- Tip uyumsuzlukları
- Tanımsız değişken kullanımı
- Sıfıra bölme hataları

## Gelecek Özellikler

1. Dizi ve koleksiyon yapıları
2. Nesne yönelimli programlama özellikleri
3. Dosya işleme fonksiyonları
4. Matematik ve string işleme kütüphaneleri

## Katkıda Bulunma

GökDil, açık kaynaklı bir projedir. Katkılarınızı bekliyoruz:
1. Yeni özellikler ekleyin
2. Hataları düzeltin
3. Dokümantasyonu geliştirin
4. Test dosyaları oluşturun


