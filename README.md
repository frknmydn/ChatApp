# ChatApp
Java ve Firebase RealtimeDatabase kullanarak yazdığım, verified edilmemiş kullanıcıların kullanamadığı, mail ve şifre ile basit kullanıcı kaydı ve girişi olan 
mesajlaşma uygulaması.

İlk defa konuşacak iki kişi için chatinbox oluşturulur ve oluşturulan inbox key'in id'sine göre mesajlar farklı bir document içinde saklanır. Mesajların sıralaması
ChatLast documenti sayesinde son gönderilen mesajın id'si ile yapılır.
