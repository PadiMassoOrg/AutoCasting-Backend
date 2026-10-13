-- Last run of each manually triggered maintenance task (e.g. the cleanup of dead event images), so an
-- admin can see when it was last run. One row per task; modified_at / modified_by record the last run's author.

CREATE TABLE maintenance_task_run (
  id                   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  task_code            varchar(100) NOT NULL,
  last_run_at          timestamp NOT NULL,
  last_deleted_objects integer NOT NULL DEFAULT 0,
  created_at           timestamp NULL DEFAULT now(),
  created_by           varchar(255) NULL,
  modified_at          timestamp NULL DEFAULT now(),
  modified_by          varchar(255) NULL,
  deleted              boolean NOT NULL DEFAULT false
);

CREATE UNIQUE INDEX ux_maintenance_task_run_task_code
  ON maintenance_task_run (task_code);
