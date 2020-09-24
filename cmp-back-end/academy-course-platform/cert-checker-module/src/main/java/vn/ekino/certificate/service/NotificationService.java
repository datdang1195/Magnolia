package vn.ekino.certificate.service;

import info.magnolia.jcr.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import vn.ekino.certificate.model.data.Notification;
import vn.ekino.certificate.repository.NotificationRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.RepositoryException;
import java.util.List;

@Singleton
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Inject
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void viewNotification(Notification data) {
        notificationRepository.findById(data.getNotificationId()).ifPresent(itm -> {
            try {
                List<String> participants = PropertyUtil.getValuesStringList(itm.getProperty(NotificationRepository.PROPERTY_PARTICIPANTS).getValues());
                List<String> trainers = PropertyUtil.getValuesStringList(itm.getProperty(NotificationRepository.PROPERTY_TRAINERS).getValues());
                List<String> supervisors = PropertyUtil.getValuesStringList(itm.getProperty(NotificationRepository.PROPERTY_SUPERVISORS).getValues());
                participants.remove(data.getUserId());
                trainers.remove(data.getUserId());
                supervisors.remove(data.getUserId());
                PropertyUtil.setProperty(itm, NotificationRepository.PROPERTY_PARTICIPANTS, participants);
                PropertyUtil.setProperty(itm, NotificationRepository.PROPERTY_TRAINERS, trainers);
                PropertyUtil.setProperty(itm, NotificationRepository.PROPERTY_SUPERVISORS, supervisors);
                notificationRepository.save(itm);
            } catch (RepositoryException e) {
                log.warn("Can't get property because {}", e.getMessage());
            }
        });
    }
}
