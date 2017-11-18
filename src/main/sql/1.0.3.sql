alter table incoming_file rename column downloaded to feed;
alter table incoming_file alter column feed default set true;
