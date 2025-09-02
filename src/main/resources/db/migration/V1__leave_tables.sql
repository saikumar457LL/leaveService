
create schema if not exists leave;

create table leave.leave_type(
                           id serial primary key ,
                           code varchar(10) not null unique ,
                           description varchar(255) not null
);


create table leave.leave_balances(
                               id serial primary key ,
                               user_id uuid not null references authentication.users(uuid) on delete cascade ,
                               leave_type_id int not null references leave.leave_type(id) ,
                               available_leaves int not null default 0 check ( available_leaves >= 0 ),
                               used_leaves int not null default 0 check ( used_leaves>=0 ),
                               UNIQUE (user_id, leave_type_id)
);

create type leave.leave_states as enum (
    'APPROVED','PENDING',
    'REJECTED'
);

create table leave.leave_requests(
                               id serial primary key ,
                               user_id uuid not null references authentication.users(uuid) on delete cascade ,
                               leave_type_id int not null references leave.leave_type(id) ,
                               from_date timestamp not null ,
                               to_date timestamp not null check ( to_date>=from_date ) ,
                               leave_status leave.leave_states not null default 'PENDING' ,
                               approver_id uuid references authentication.users(uuid) ,
                               created_at timestamp not null default current_timestamp ,
                               updated_at timestamp not null default current_timestamp
);


-- =========================================
-- Indexes for leave_type
-- =========================================

-- Quickly find leave type by code (already UNIQUE, so index exists)
-- Optional: index on description for reporting
CREATE INDEX IF NOT EXISTS idx_leave_type_description
    ON leave.leave_type(description);


-- =========================================
-- Indexes for leave_balances
-- =========================================

-- Quickly find all balances for a user
CREATE INDEX IF NOT EXISTS idx_leave_balances_user
    ON leave.leave_balances(user_id);

-- Quickly find balances by leave type
CREATE INDEX IF NOT EXISTS idx_leave_balances_type
    ON leave.leave_balances(leave_type_id);

-- UNIQUE(user_id, leave_type_id) already creates an index
-- No need to create a separate composite index unless reporting needs it
-- Optional: composite index for reporting all balances by leave_type and user
CREATE INDEX IF NOT EXISTS idx_leave_balances_type_user
    ON leave.leave_balances(leave_type_id, user_id);


-- =========================================
-- Indexes for leave_requests
-- =========================================

-- Quickly get all requests for a specific user
CREATE INDEX IF NOT EXISTS idx_leave_requests_user
    ON leave.leave_requests(user_id);

-- Quickly find requests by leave status (e.g., PENDING)
CREATE INDEX IF NOT EXISTS idx_leave_requests_status
    ON leave.leave_requests(leave_status);

-- Quickly filter requests by leave type
CREATE INDEX IF NOT EXISTS idx_leave_requests_type
    ON leave.leave_requests(leave_type_id);

-- Quickly find requests assigned to an approver
CREATE INDEX IF NOT EXISTS idx_leave_requests_approver
    ON leave.leave_requests(approver_id);

-- Common dashboard query: all pending requests for a specific user
CREATE INDEX IF NOT EXISTS idx_leave_requests_user_status
    ON leave.leave_requests(user_id, leave_status);

-- Optional: reporting queries to get all approved requests by leave_type
CREATE INDEX IF NOT EXISTS idx_leave_requests_type_status
    ON leave.leave_requests(leave_type_id, leave_status);

-- Optional: combined index for queries filtering by approver and status
CREATE INDEX IF NOT EXISTS idx_leave_requests_approver_status
    ON leave.leave_requests(approver_id, leave_status);


-- =======================
-- Initial data
-- =======================
insert into leave.leave_type(code, description) values
                                              ('SL','Sick leave') ,
                                              ('CL','Casual leave') ,
                                              ('EL','Earned leave') ,
                                              ('CML','Camp-Off leave') ,
                                              ('ML','Maternity leave');