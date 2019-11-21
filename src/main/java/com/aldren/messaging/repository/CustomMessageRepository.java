package com.aldren.messaging.repository;

import java.util.Date;

public interface CustomMessageRepository {

    int messageCountByDateDuration(Date startDate, Date endDate);

}
