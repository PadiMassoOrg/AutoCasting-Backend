-- Proposals system owner: a technical user + employer profile ("Autocasting") that owns every
-- proposal's content (e.g. a casting) while the proposal is PENDING. Claiming a proposal reassigns
-- that content to the authenticated user. The user can never log in: no password, provider OTHER,
-- suspended. Fixed UUIDs, mirrored in AppConstants.PROPOSALS_SYSTEM_*.

INSERT INTO public.users (
  id, created_at, created_by, deleted, modified_at, modified_by,
  email, password, user_account_provider, active_mode,
  talent_onboarding_status, employer_onboarding_status, suspended
)
VALUES (
  '00000000-0000-0000-0000-000000000a01', NOW(), 'FLYWAY_V56', false, NOW(), 'FLYWAY_V56',
  'proposals@system.autocasting.app', NULL, 'OTHER', 'EMPLOYER',
  'NOT_STARTED', 'COMPLETED', true
);

INSERT INTO public.employer_profile (
  id, created_at, created_by, deleted, modified_at, modified_by,
  default_slug, premium_slug, plan_id, user_id
)
SELECT
  '00000000-0000-0000-0000-000000000a02', NOW(), 'FLYWAY_V56', false, NOW(), 'FLYWAY_V56',
  'AC-PROPOSALS', NULL, p.id, '00000000-0000-0000-0000-000000000a01'
FROM public."plans" p
WHERE p.code = 'FREE';

INSERT INTO public.employer_basic_info (
  id, created_at, created_by, deleted, modified_at, modified_by,
  company_name, employer_profile_id
)
VALUES (
  '00000000-0000-0000-0000-000000000a03', NOW(), 'FLYWAY_V56', false, NOW(), 'FLYWAY_V56',
  'Autocasting', '00000000-0000-0000-0000-000000000a02'
);

-- Casting Proposal target. "A CASTING proposal must have a casting_id" is enforced in the service
-- (CastingProposalServiceImpl): a CHECK can't reference proposal_type_option to resolve the type.
ALTER TABLE proposals
  ADD COLUMN casting_id uuid NULL,
  ADD CONSTRAINT uk_proposals_casting_id UNIQUE (casting_id),
  ADD CONSTRAINT fk_proposals_casting
    FOREIGN KEY (casting_id) REFERENCES casting (id);
