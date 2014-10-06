alter table prestamo
add column pre_tipo VARCHAR(15) NOT NULL DEFAULT 'MENSUAL';

alter table prestamo
drop column pre_cant_meses;

alter table prestamo
add column pre_cuota_pura decimal(11) NOT NULL DEFAULT 0;

alter table prestamo
drop column pre_importe_cuota;

alter table prestamo
add column pre_monto_total decimal(11) NOT NULL DEFAULT 0;

INSERT INTO `prestamo` VALUES (1,12,'10000.00','VIGENTE','2013-08-03 19:03:32','2011-04-09 00:00:00',4.50,1,'450.00','MENSUAL','833','10450.00')
